HamiNavodayaHo - A Comprehensive Social Media Platform

HamiNavodayaHo is a feature-rich social media application designed to connect people, facilitate communication, and enable the sharing of moments and events. Built with a robust Django backend and a native Android frontend, it offers a seamless and interactive user experience.
Table of Contents

    About the Project

    Features

    Technologies Used

    Prerequisites

    Installation

        Backend (Django) Setup

        Frontend (Android) Setup

    Usage

    Contributing

About the Project

HamiNavodayaHo aims to provide a complete social networking experience, allowing users to create profiles, share updates, engage in real-time conversations, and manage events. The application is divided into two main components: a powerful backend API developed with Django, handling data storage, user authentication, and business logic, and a responsive Android application built with Java, providing an intuitive user interface.
Features

HamiNavodayaHo comes packed with a variety of features to enhance user interaction:

    User Authentication: Secure user registration, login, and session management.

    User Profiles:

        Create and customize personal profiles.

        View other users' profiles.

        Edit profile information (bio, profile picture, etc.).

    Posts & Feed:

        Create and share text, image, and video posts.

        Like, comment on, and share posts.

        Personalized news feed displaying posts from friends/followed users.

    Events Management:

        Create and manage events (public/private).

        RSVP to events.

        View event details, attendees, and updates.

    Chat System:

        Real-time one-on-one and group messaging.

        Send text messages, emojis, and media.

    Voice/Video Calls:

        Integrated calling functionality (details on specific implementation, e.g., WebRTC, would go here if known).

    Friendship/Following System:

        Send, accept, and decline friend requests.

        Follow/unfollow other users.

    Notifications: Real-time notifications for new messages, likes, comments, friend requests, and event updates.

    Search Functionality: Search for users, posts, and events.

Technologies Used

This project leverages a modern tech stack to ensure performance, scalability, and a great user experience.
Frontend

    Android Studio: Integrated Development Environment for Android.

    Java: Primary programming language for Android application development.

    XML: For UI layout design.

    Request: For making HTTP requests to the Django API.

    Glide/Picasso: For efficient image loading and caching.

    AndroidX Libraries: Modern Android support libraries.

Backend

    Django: High-level Python web framework for rapid development and clean design.

    Django REST Framework (DRF): Toolkit for building Web APIs with Django.

    Python: Programming language for the backend logic.

    PostgreSQL: Database system(PostgreSQL).

    Django Channels: For WebSocket communication (essential for real-time chat/calls).

Prerequisites

Before you begin, ensure you have the following installed on your system:

    For Backend:

        Python 3.9+

        pip (Python package installer)

        Virtual environment tool (e.g., venv)

        Database system (e.g., PostgreSQL, MySQL).

    For Frontend:

        Android Studio (latest version recommended)

        Java Development Kit (JDK) 11+

        An Android device or emulator for testing(e.g., GenyMotion).

Installation

Follow these steps to get your development environment set up.
Backend (Django) Setup

    Clone the repository:

    git clone https://github.com/pryogendra/haminavodayaho.git
    cd haminavodayaho

    Create and activate a virtual environment:

    python -m venv venv
    # On Windows
    .\venv\Scripts\activate
    # On macOS/Linux
    source venv/bin/activate

    Install dependencies:

    pip install -r requirements.txt

    Run database migrations:

    python manage.py migrate

    Create a superuser (optional, for admin access):

    python manage.py createsuperuser

    Start the Django development server:

    python manage.py runserver

    The backend API will now be running at http://127.0.0.1:8000/.

Frontend (Android) Setup

    Open Android Studio:
    Launch Android Studio and select "Open an existing Android Studio project".

    Navigate to the Frontend Project:
    Browse to the APP-HamiNavodayaHo directory (or wherever your Android project is located) and open it.

    Sync Gradle:
    Android Studio will automatically try to sync the Gradle project. If it prompts, install any missing SDK components or build tools.

    Configure Backend API URL:
    You will need to update the base URL for your backend API in the Android project. This is typically found in a Constants.java, NetworkConfig.java, or similar file.
    Locate the file and change BASE_URL to your Django backend's address (e.g., http://10.0.2.2:8000/ if running on an Android emulator, or your machine's IP address if testing on a physical device connected to the same network).

    Run the Application:
    Connect an Android device or start an emulator. Click the "Run" button (green play icon) in Android Studio to deploy and run the application.

Usage

Once both the backend server is running and the Android application is installed:

    Register: Create a new user account on the Android app.

    Login: Use your new credentials to log in.

    Explore:

        Navigate through the feed to see posts.

        Create your own posts.

        Discover and join events.

        Start chats with other users.

        Update your profile.

Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are greatly appreciated.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

