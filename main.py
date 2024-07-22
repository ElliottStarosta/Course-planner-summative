# <==++ Standard Library Imports ++==>
import pickle
import string

# <==++ Third-Party Library Imports ++==>
import pandas as pd
import uvicorn
from fastapi import FastAPI, Query
from gensim.models import Word2Vec
import nltk
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from pydantic import BaseModel
from sklearn.ensemble import RandomForestClassifier
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics import accuracy_score
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.model_selection import train_test_split
from spellchecker import SpellChecker

# <===++ Local Imports ++==>
from categories import categories

# <===++ Load pre-trained data ++==>
nltk.download('punkt')
nltk.download('stopwords')

# <===++ FastAPI setup ++===>
app = FastAPI()

# <==++ Defines the pydantic model that represents incoming requests to the API endpoint ++==>
class RecommendRequest(BaseModel):
    interests: str

# <===++ Handles data preprocessing tasks ++==>
class DataProcessor:
    def __init__(self, file_path):
        self.file_path = file_path

    def preprocess_and_save(self, model_file):
        df = pd.read_excel(self.file_path, sheet_name="Sheet1")

        # Encode Course Name as categorical IDs
        df["Course Code"] = pd.Categorical(df["Course Code"])
        df["Course ID"] = df["Course Code"].cat.codes

        # TF-IDF vectorization for 'Interests Tags'
        tfidf_vectorizer = TfidfVectorizer(max_features=1000)
        interests_tfidf = tfidf_vectorizer.fit_transform(
            df["Interests Tags"].fillna("")
        ).toarray()
        interests_tfidf_df = pd.DataFrame(
            interests_tfidf, columns=tfidf_vectorizer.get_feature_names_out()
        )

        # Encode Course Area as binary indicators
        for area in df["Course Area"].unique():
            df[f"Area_{area}"] = df["Course Area"].apply(
                lambda x: 1 if x == area else 0
            )

        # Combine encoded features with TF-IDF features and binary indicators for 'Course Area'
        df_final = pd.concat(
            [
                df.drop(
                    columns=[
                        "Interests Tags",
                        "Course Area",
                        "Prerequisites",
                        "Track",
                        "Course Name",
                        "Graduation Requirement",
                    ]
                ),  # Drop unnecessary columns
                interests_tfidf_df,  # TF-IDF features for 'Interests Tags'
                df[
                    [f"Area_{area}" for area in df["Course Area"].unique()]
                ],  # Include binary indicators for 'Course Area'
            ],
            axis=1,
        )

        # Ensure all columns are numeric and handle any potential issues
        df_final = df_final.apply(pd.to_numeric, errors="coerce").fillna(0)

        # Define the target variable
        y = df["Course ID"]
        X_train, X_test, y_train, y_test = train_test_split(
            df_final, y, test_size=0.2, random_state=42
        )

        # Train a random forest classifier
        model = RandomForestClassifier(random_state=42)
        model.fit(df_final, y)


        # Save the model and TF-IDF vectorizer
        with open(model_file, "wb") as f:
            pickle.dump((model, tfidf_vectorizer), f)

# <===++ Loads the trained model and course data, and computes then recommends the student their classes ++===>
class CourseRecommendation:
    def __init__(self, model_file, df_file, multi_w2v):
        self.model_file = model_file
        self.df_file = df_file
        self.multi_w2v = multi_w2v
        # Courses it cannot recommend 
        self.excluded_course_codes = [
            "ENL1W", "MTH1W", "SNC1W", "CGC1W", "ENG2D", "MPM2D", "SNC2D",
            "CHC2D", "CHV2O", "NBE3U", "MCR3U", "ENG4U", "MHF4U", "MCV4U",
            "NBE3C", "MBF3C", "ENG4C"
        ]
    # <===++ Preprocesses student interests using SpellChecker and Word2Vec for semantic similarity, and then recommend courses ++==>
    def recommend_classes(self, student_input):
        try:
            with open(self.model_file, "rb") as f:
                model, tfidf_vectorizer = pickle.load(f)

            # Load the course data from the Excel file
            df = pd.read_excel(self.df_file, sheet_name="Sheet1")

            # Preprocess student interests using SpellChecker
            corrected_interests = SpellCheck.suggest_correct_word(student_input["interests"])

            # Preprocess student interests using Word2Vec for semantic similarity
            processed_interests = self.multi_w2v.preprocess_student_interests(corrected_interests)

            # Extract TF-IDF for processed student interests
            interests_tfidf = tfidf_vectorizer.transform([processed_interests]).toarray()

            similarities = []
            # Calculate cosine similarity between student interests and each course
            for index, row in df.iterrows():
                if row["Course Code"] not in self.excluded_course_codes:
                    course_interests_tfidf = tfidf_vectorizer.transform([row["Interests Tags"]]).toarray()
                    similarity = cosine_similarity(interests_tfidf, course_interests_tfidf)[0][0]
                    if similarity:
                        similarities.append((row["Course Code"], row["Course Name"], similarity))

            # Sort courses by similarity in descending order
            similarities.sort(key=lambda x: x[2], reverse=True)

            #Limit the number of recommended courses to 20 (this is the max number of open spots)
            top_similarities = similarities[:20]

            # Handle case where no similar courses are found
            if not similarities:
                print("No recommended courses found.")
                return -1

            # Format recommended courses into a list of dictionaries
            recommended_courses = [{"Course Code": course[0], "Course Name": course[1]} for course in top_similarities]

            return recommended_courses

        except Exception as e:
            print(f"Error in recommending courses: {e}")
            return -1



# <===++ SpellCheck class for correcting student interests ++===>
class SpellCheck:
    @staticmethod
    def suggest_correct_word(interests):
        try:
            # Initialize a SpellChecker instance from the spellchecker library
            spell = SpellChecker()

            # Tokenize and convert interests to lowercase
            interests_words = word_tokenize(interests.lower())

            # Remove punctuation from the tokenized words
            interests_words = [word for word in interests_words if word not in string.punctuation]


             # Remove stopwords from the tokenized words
            stop_words = set(stopwords.words('english'))
            stop_words.update(['like']) # custom word for 'like' as it is not in the set...

            interests_words = [word for word in interests_words if word not in stop_words]
            

            # Use the SpellChecker instance to correct each word
            corrected_words = [spell.correction(word) if spell.unknown([word]) else word for word in interests_words]

            # Join corrected words into a sentence
            corrected_sentence = " ".join(corrected_words)

            return corrected_sentence


        except Exception as e:
            # Handle any exceptions that occur during spell checking
            print(f"Error in spell checking: {e}")
            return interests


#<==++ Word vectorizing for synonyms & mapping ++===>
class MultiCategoryWord2Vec:
    def __init__(self, vector_size=100, window=5, min_count=1, workers=4):
        self.vector_size = vector_size
        self.window = window
        self.min_count = min_count
        self.workers = workers
        self.categories = categories
        self.models = self.train_models()

    #<==++ Train the models based on caloric data ++===>
    def train_models(self):
        models = []
        for category in self.categories:
            model = Word2Vec([category], vector_size=self.vector_size, window=self.window,
                             min_count=self.min_count, workers=self.workers)
            models.append(model)
        return models

    # <==++ Find similar words within categories using Word2Vec ++===>
    def find_similar_words(self, word, topn=3):
        results = []
        for idx, model in enumerate(self.models):
            if word in model.wv.key_to_index:
                similar_words = model.wv.most_similar(word, topn=topn)
                results.extend([(word, score) for word, score in similar_words])
        return results

    # <==++ Preprocess student interests by finding similar words within categories using Word2Vec ++===>
    def preprocess_student_interests(self, interests, topn=3):
        try:
            # Tokenize student interests
            tokens = word_tokenize(interests.lower())

            # Placeholder for processed interests (using Word2Vec for finding related terms)
            processed_interests = []

            # Iterate over tokens and find similar words within categories
            for token in tokens:
                similar_words = self.find_similar_words(token, topn=topn)

                if similar_words:
                    similar_tokens = [word for word, _ in similar_words[:topn]]
                    processed_interests.extend([token] + similar_tokens)
                else:
                    processed_interests.append(token)  # Keep the original token if no similar word found

            # Join processed interests back into a string
            preprocessed_interests = " ".join(processed_interests)

            return preprocessed_interests

        except Exception as e:
            print(f"Error in preprocessing student interests: {e}")
            return interests




# <==++ FastAPI Endpoint for Course Recommendations ++==>
@app.get("/recommend-courses/")
def get_recommendations(interests: str = Query(..., description="Sentence describing the student's interests.")):
    try:

        # Prepare student input dictionary for course recommendation
        student_input = {"interests": interests}

        # Initialize CourseRecommendation object with model and data files
        course_rec = CourseRecommendation(model_file="courses_model.pkl", df_file="CoursesFinal.xlsx", multi_w2v=multi_w2v)

        # Get recommended courses based on student's corrected interests
        recommended_courses = course_rec.recommend_classes(student_input)

        # Handle case where no courses are recommended
        if recommended_courses == -1:
            return {"message": "No recommended courses found."}

        # Return recommended courses as a JSON response
        return recommended_courses

    except Exception as e:
        # Handle any exceptions that occur during course recommendation process
        return {"error": f"An error occurred: {e}"}
    

    
# <===++ Main function to run the API ++===>
multi_w2v = MultiCategoryWord2Vec()

if __name__ == "__main__":
    input_file = "CoursesFinal.xlsx"
    model_file = "courses_model.pkl"


    # Preprocess and save the course data and trained model.
    data_processor = DataProcessor(input_file)
    data_processor.preprocess_and_save(model_file)

    # Initialize CourseRecommendation object with model, data files, and Word2Vec model
    course_rec = CourseRecommendation(model_file=model_file, df_file=input_file, multi_w2v=multi_w2v)
    # Starts the FastAPI server
    uvicorn.run(app, host="127.0.0.1", port=8001)