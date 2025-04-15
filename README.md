# ✈️ Travel guide chat

This app allows users to chat with an LLM-powered travel assistant to begin planning their journeys. It supports session history, intelligent title generation, and a polished Jetpack Compose UI.


---

## 🧠 Features

- 🗺️ **LLM Travel Assistant** – Powered by OpenRouter (ChatGPT-compatible) for dynamic travel conversations  
- 💬 **Chat Session History** – View, rename, and re-engage with previous conversations  
- 🧠 **Intelligent Titles** – Auto-generated based on chat content for quick reference  
- ⬇️ **Scroll-to-Bottom UX** – Seamless messaging experience  
- 📝 **Markdown Support** – Clean rendering of assistant responses  
- 💾 **Persistent Storage** – Session data saved locally as JSON for offline access  
- 🧪 **Clean Architecture** – Built with Jetpack Compose, MVVM, and Ktor

---

## 🚀 Tech Stack

| Layer        | Technology                     |
|--------------|--------------------------------|
| UI           | Jetpack Compose                |
| State Mgmt   | ViewModel, StateFlow           |
| Backend API  | OpenRouter (ChatGPT-compatible)|
| Storage      | JSON File (local)              |
| Networking   | Ktor                           |
| Build Tools  | Kotlin, Gradle, Android Studio |

---


## 🧠 Design Overview

- Built using **Jetpack Compose** for a clean, modern UI  
- Integrates **OpenRouter** API using **Ktor**  
- Chat sessions are cached **locally** via `ChatStorage` (as JSON files)  
- Markdown support for LLM responses (rendered to plain text)  
- Conversations start with a friendly **system prompt**  
- Includes **back button navigation**, timestamp formatting, and scroll-to-latest message behavior  

---

## ▶️ Instructions for Running the Project

1. Clone the repo** and open it in Android Studio:

```bash
git clone https://github.com/your-username/travel-guide-chat.git
```

2. Create an api.properties file in the root of your project:
   
```bash
API_KEY=your_openrouter_api_key_here
```
You can generate a free API key from https://openrouter.ai

3. Update build.gradle.kts (Module: app)

At the top:
```bash
import java.util.Properties

val apiProperties = Properties().apply {
    load(File(rootDir, "api.properties").inputStream())
}
```
Inside the android block:

```bash
buildFeatures {
    buildConfig = true
}

defaultConfig {
    ...
    buildConfigField("String", "API_KEY", "\"${apiProperties["API_KEY"]}\"")
}
```
Run the App on an emulator or physical device using the ▶️ button in Android Studio.

---

## 🎥 App Walkthrough

<img src="Walkthrough.gif" width="300"/>

[View on Imgur](https://imgur.com/a/KwLTxfu)

---

## 🛠 Future Enhancements

🔍 In-chat search

🌐 Retrieval-Augmented Generation (RAG)

🧠 Pinecone vector DB integration

☁️ Cloud sync and user auth

## 🔐 Note
This app requires a valid OpenRouter API key defined as API_KEY in api.properties. The key is not included in version control.

## 🧑‍💻 Author
Built by Kevin Hui — Kotlin/AI/Full-stack engineer focused on building delightful, scalable products.

## MIT License

Copyright (c) 2025 Kevin Hui

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the “Software”), to deal
in the Software without restriction, including without limitation the rights  
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
copies of the Software, and to permit persons to whom the Software is  
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in  
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN  
THE SOFTWARE.
