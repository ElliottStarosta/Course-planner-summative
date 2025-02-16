# 🎓 Welcome to EOM Course Recommender!



#### 📅 Date Completed: Jan 20, 2025

#### 👨‍💻 Author: Elliott Starosta

## 📌 Description

The **EOM Course Recommender** is a Java-based application designed to help students find suitable courses based on their preferences and requirements. The application uses a machine learning recommendation algorithm to suggest courses that align with user interests, previous classes, and future career goals, providing a personalized learning experience.

🔗 **Find the Python ML API on the API branch**: [API Branch](https://github.com/ElliottStarosta/Course-planner-summative/tree/API).

## 📜 Table of Contents

- [🚀 Getting Started](#-getting-started)
- [🛠️ Usage](#-usage)
- [✨ Features](#-features)
- [📊 UML Diagram](#-uml-diagram)
- [📂 Project Structure](#-project-structure)
- [📜 License](#-license)

## 🚀 Getting Started

Follow these steps to get your development environment set up:

### ✅ Prerequisites

- Java Development Kit (JDK) 11 or higher
- Maven 3.6 or higher
- Git

### 📥 Cloning the Repository

First, clone this repository to your local machine:

```bash
git clone https://github.com/ElliottStarosta/Course-planner-summative.git
```

### 📦 Maven Dependency Installation

```bash
cd CourseSummative
mvn install
```

## 🛠️ Usage

Run **org.example.gui.pages.Application** to start the program.

## ✨ Features

- 🎯 Personalized course recommendations based on user profiles and preferences.
- 🤖 Integration of machine learning algorithms for accurate suggestions.
- 🖥️ User-friendly interface for easy interaction and navigation.
- 📞 Counselor contact to arrange your classes.

## 📊 UML Diagram

You can view the UML diagrams [here](https://docs.google.com/document/d/1RnHR5U51fuQDec1-17yL2D_yQI_z8zwKZ3HttHLKJSo/edit?usp=sharing).

## 📂 Project Structure

```
EOMCourseRecommender
├── src
│   └── main
│       └── java
│           └── org
│               └── example
│                   ├── gui
│                   │   ├── component
│                   │   │   ├── account
│                   │   │   │   ├── CreateAccount.java
│                   │   │   │   ├── ForgotPasswordUtil.java
│                   │   │   │   └── TwoFactorAuthentication.java
│                   │   │   ├── jcomponents
│                   │   │   │   ├── ComboBox.java
│                   │   │   │   ├── PageMenuIndicator.java
│                   │   │   │   └── PasswordStrengthStatus.java
│                   │   ├── manager
│                   │   │   ├── DynamicFormLoader.java
│                   │   │   ├── FormsManager.java
│                   │   │   └── NotificationManager.java
│                   │   ├── pages
│                   │   │   ├── Application.java
│                   │   │   ├── login
│                   │   │   │   ├── ForgotPasswordForm.java
│                   │   │   │   ├── LoginForm.java
│                   │   │   │   ├── PasswordChangeForm.java
│                   │   │   │   ├── RegisterForm.java
│                   │   │   │   └── VerificationForm.java
│                   │   │   ├── main
│                   │   │   │   ├── DashboardForm.java
│                   │   │   │   └── EditButtonListener.java
│                   │   │   └── quiz
│                   │   │       ├── FillCourses.java
│                   │   │       ├── Form1.java
│                   │   │       ├── Form2.java
│                   │   │       ├── Form3.java
│                   │   │       ├── Form4.java
│                   │   │       └── Form5.java
│                   ├── people
│                   │   ├── Counselor.java
│                   │   ├── StudentCounselor.java
│                   │   ├── User.java
│                   │   └── UserInput.java
│                   └── utility
│                       ├── EncryptionUtil.java
│                       ├── JsonUtil.java
│                       ├── UsersUtil.java
│                       └── api
│                           ├── APIClient.java
│                           ├── Deployment.java
│                           ├── PythonAPI.java
│                           └── email
│                               ├── EmailUtil.java
│                               └── SendEmail.java
│                       └── courses
│                           ├── Course.java
│                           ├── CourseAssembly.java
│                           ├── ExcelUtility.java
│                           └── JsonToPdfConverter.java
```

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
