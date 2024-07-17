# <===++ Imports ++===>

import pickle
import pandas as pd
import uvicorn
from fastapi import FastAPI, Query
from pydantic import BaseModel
from sklearn.ensemble import RandomForestClassifier
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics import accuracy_score
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.model_selection import train_test_split

from spellchecker import SpellChecker
from nltk.stem import PorterStemmer
from nltk.tokenize import word_tokenize
import nltk
import os
import joblib

nltk.download('punkt')

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

        y_pred = model.predict(X_test)

        # Calculate accuracy
        accuracy = accuracy_score(y_test, y_pred)
        print(f"Model Accuracy: {accuracy:.2f}")

        # Save the model and TF-IDF vectorizer
        with open(model_file, "wb") as f:
            pickle.dump((model, tfidf_vectorizer), f)

# <===++ Loads the trained model and course data, and computes then recommends the student their classes ++==>

class CourseRecommendation:
    def __init__(self, model_file, df_file):
        self.model_file = model_file
        self.df_file = df_file

    def recommend_classes(self, student_input):
        try:
            with open(self.model_file, "rb") as f:
                model, tfidf_vectorizer = pickle.load(f)

            # Load the course data from the Excel file
            df = pd.read_excel(self.df_file, sheet_name="Sheet1")

            # Extract student interests from input and transform to TF-IDF vector
            interests = student_input["interests"]
            interests_tfidf = tfidf_vectorizer.transform([interests]).toarray()

            similarities = []
            # Calculate cosine similarity between student interests and each course
            for index, row in df.iterrows():
                course_interests_tfidf = tfidf_vectorizer.transform([row["Interests Tags"]]).toarray()
                similarity = cosine_similarity(interests_tfidf, course_interests_tfidf)[0][0]
                if similarity:
                    similarities.append((row["Course Code"], row["Course Name"], similarity))

            # Sort courses by similarity in descending order
            similarities.sort(key=lambda x: x[2], reverse=True)

            # Handle case where no similar courses are found
            if not similarities:
                print("No recommended courses found.")
                return -1

            # Format recommended courses into a list of dictionaries
            recommended_courses = [{"Course Code": course[0], "Course Name": course[1]} for course in similarities]

            return recommended_courses

        except Exception as e:
            print(f"Error in recommending courses: {e}")
            return -1

# <===++ Spell checks interests ++==>

class SpellCheck:
    @staticmethod
    def suggest_correct_word(interests):
        try:
            # Initialize a SpellChecker instance from the spellchecker library
            spell = SpellChecker()

            # Tokenize and convert interests to lowercase
            interests_words = word_tokenize(interests.lower())

            # Define a custom stemming function using PorterStemmer
            def custom_stem(word):
                if word.endswith('ing'):
                    return PorterStemmer().stem(word)
                else:
                    return word

            # Apply custom stemming to each word in interests
            stemmed_words = [custom_stem(word) for word in interests_words]

            # Use the SpellChecker instance to correct each stemmed word
            corrected_words = [spell.correction(word) if spell.correction(word) is not None else word for word in stemmed_words]

            # Join corrected words into a sentence
            corrected_sentence = " ".join(corrected_words)

            return corrected_sentence

        except Exception as e:
            # Handle any exceptions that occur during spell checking
            print(f"Error in spell checking: {e}")
            return interests


# <==++ FastAPI Endpoint for Course Recommendations ++==>

@app.get("/recommend-courses/")
def get_recommendations(interests: str = Query(..., description="Sentence describing the student's interests.")):
    try:
        # Correct student's interests using SpellCheck class
        corrected_interests_sentence = SpellCheck.suggest_correct_word(interests)

        # Prepare student input dictionary for course recommendation
        student_input = {"interests": corrected_interests_sentence}

        # Initialize CourseRecommendation object with model and data files
        course_rec = CourseRecommendation(model_file="courses_model.pkl", df_file="CoursesFinal.xlsx")

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


# <===++ Main function to run the API ++==>
import os

if __name__ == "__main__":
    # input_file = "CoursesSummative/src/main/CoursesFinal.xlsx" 
    # model_file = "CoursesSummative/src/main/courses_model.pkl"

    input_file = "CoursesFinal.xlsx"
    model_file = "courses_model.pkl"

    # Preprocess and save the course data and trained model.
    data_processor = DataProcessor(input_file)
    data_processor.preprocess_and_save(model_file)

    # Starts the FastAPI server
    uvicorn.run(app, host="127.0.0.1", port=8000)