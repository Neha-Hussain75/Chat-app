# 💬 Chatter – README

## 📱 Overview

Chatter is a sleek and modern real-time chat application built with **Android (Jetpack Compose + Kotlin)** and **Firebase Realtime Database**. The app provides smooth, minimal, and responsive UI with focus on performance and real-time interactions.

---

## ✨ Features

* **User Authentication** via Firebase Auth (Email/Password)
* **Real-time Messaging** using Firebase Realtime Database
* **Conversations List** displaying active chats
* **Individual Chat Screen** with sent/received message bubbles
* **Timestamps on Messages** (auto-synced)
* **Modern UI** with Compose: cards, rounded shapes, and clean layout
* **Lightweight Data Model** designed for scalability

---

## 🔒 Security Rules (Firebase)

* Only authenticated users can read/write.
* Users can only access conversations they are part of.
* Message data is protected per conversation.

---

## 🚀 Setup Guide

1. Clone the repository.
2. Open in Android Studio.
3. Connect Firebase project .
4. Add `google-services.json` file in `/app` folder.
5. Build & Run the app.

---

## ⚠️ Notice – google-services.json

This project includes a `google-services.json` file only for **demo/testing purposes**.

* The Firebase project linked to this file is **not used in production**.
* No real or sensitive user data is stored or processed.
* No billing is enabled on the Firebase project.

⚡ If you plan to make a similar app, please **generate and use your own `google-services.json`** by creating a Firebase project.

❌ Do **not** reuse the included Firebase config in production apps.

---

## 🖼 Screenshots

<p align="center">
  <img src="screenshots/splashscreen.png" alt="Splash Screen" width="250"/>
  <img src="screenshots/login.png" alt="Login Screen" width="250"/>
  <img src="screenshots/register.png" alt="Register Screen" width="250"/>
</p>

<p align="center">
  <img src="screenshots/homescreen.png" alt="Home Screen" width="250"/>
  <img src="screenshots/chatscreen.png" alt="Chat Screen" width="250"/>
  <img src="screenshots/setting.png" alt="Settings Screen" width="250"/>
</p>

<p align="center">
  <img src="screenshots/editprofile.png" alt="Edit Profile Screen" width="250"/>
  <img src="screenshots/password.png" alt="Password Screen" width="250"/>
</p>



