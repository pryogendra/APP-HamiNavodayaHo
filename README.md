HamiNavodayaHo - A Comprehensive Social Media Platform

HamiNavodayaHo aims to provide a complete social networking experience, allowing users to create profiles, share updates, engage in real-time conversations, and manage events. The application is divided into two main components: a powerful backend API developed with Django, handling data storage, user authentication, and business logic, and a responsive Android application built with Java, providing an intuitive user interface.
Features

ScreenShots 
<img width="720" height="1612" alt="Screenshot_20250517-110209" src="https://github.com/user-attachments/assets/d86a80fc-6025-460a-85f4-b44b1aa3fe11" />
<img width="720" height="1612" alt="Screenshot_20250517-103438" src="https://github.com/user-attachments/assets/bcf3d338-ff34-4861-8556-b3fbf0b16894" />
<img width="720" height="1612" alt="Screenshot_20250517-103251" src="https://github.com/user-attachments/assets/84496464-f404-482f-a407-6c386db4f30e" />
<img width="720" height="1612" alt="Screenshot_20250517-103225" src="https://github.com/user-attachments/assets/0ef14712-6071-4ee6-bb21-3410f23b3191" />
<img width="720" height="1612" alt="Screenshot_20250517-103245" src="https://github.com/user-attachments/assets/ced7dded-b669-44ec-a275-0d88d0406ee6" />
<img width="720" height="1612" alt="Screenshot_20250517-103236" src="https://github.com/user-attachments/assets/973b265a-fa3c-429d-962c-07927abebbf5" />
<img width="720" height="1612" alt="Screenshot_20250517-103229" src="https://github.com/user-attachments/assets/35b6193c-263c-41de-87a8-d99b4a246709" />
<img width="720" height="1612" alt="Screenshot_20250517-103212" src="https://github.com/user-attachments/assets/2eaae41c-4b53-4221-b4ef-1d461a7409f4" />
<img width="720" height="1612" alt="Screenshot_20250517-094627" src="https://github.com/user-attachments/assets/d286e4ef-3267-4aa9-b767-6fadb9600d82" />
<img width="720" height="1612" alt="Screenshot_20250517-094613" src="https://github.com/user-attachments/assets/e2a6b1b3-dccc-4beb-9e3b-d1f188ab2556" />
<img width="720" height="1612" alt="Screenshot_20250517-094559" src="https://github.com/user-attachments/assets/cc78075b-a6e6-41f7-badd-122604b46fcd" />
<img width="720" height="1612" alt="Screenshot_20250517-094640" src="https://github.com/user-attachments/assets/d65ff820-dc6f-432c-8d01-879e0abc85a0" />
<img width="720" height="1612" alt="Screenshot_20250517-094546" src="https://github.com/user-attachments/assets/b22cd1da-73bc-4a3e-ab68-46be5c720bcb" />
<img width="720" height="1612" alt="Screenshot_20250517-093532" src="https://github.com/user-attachments/assets/9aa45291-ac79-4d69-a54f-8fa1e0af5af1" />



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

