# QMoney

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Crio](https://img.shields.io/badge/Crio-Learn%20by%20Doing-FFDD00?style=for-the-badge&logo=crio)

> **Date:** May 2023

## 📘 Overview
**QMoney** is a visual stock portfolio analyzer designed to assist portfolio managers in making informed trade recommendations for their clients. This project involves building core functionality for managing stock portfolios, fetching real-time stock data, and calculating returns, with a focus on application stability, availability, and performance.

During this project:
- Implemented essential portfolio management logic and published it as a Java library (JAR).
- Refactored the application to support multiple stock quote services.
- Enhanced the application’s performance and resilience.

## 🛠️ Project Structure

### **Fetch Stock Quotes and Compute Annualized Returns**
#### Scope of Work
- Integrated **Tiingo’s REST API** to retrieve live stock data.
- Developed logic to calculate annualized returns based on stock purchase date and holding period.

#### Skills Used
- **Java**, **REST API**, **Jackson**

---

### **Refactor and Publish as JAR**
#### Scope of Work
- Refactored code to align with an updated interface contract provided by the backend team.
- Packaged the core functionality as a **JAR file** for simplified versioning and distribution.
- Created usage examples to document the library’s features.

#### Skills Used
- **Interfaces**, **Code Refactoring**, **Gradle**

---

### **Improve Application Availability and Stability**
#### Scope of Work
- Added a backup stock quote service using **Alpha Vantage** to ensure service availability.
- Enhanced stability through detailed error reporting and robust exception handling.

#### Skills Used
- **Interfaces**, **Exception Handling**

---

### **Enhance Application Performance**
#### Scope of Work
- Introduced **multithreading** to improve responsiveness and handle larger datasets more efficiently.
- Developed unit tests to assess and validate performance gains.

#### Skills Used
- **Multithreading**, **Unit Testing**

---

## 🚀 Getting Started

1. **Clone the Repository**
   ```bash
   git clone https://github.com/govindnathsng/QMoney.git
   cd QMoney

    Build the Project

    bash

./gradlew build

Run the Tests

bash

    ./gradlew test

⚙️ Technologies & Tools

    Backend: Java, REST APIs, Multithreading, Jackson
    Build Tool: Gradle
    Testing: JUnit
    Dependencies: Tiingo API, Alpha Vantage (Backup API)

📈 Features

    Real-Time Stock Quotes: Fetch current stock prices from Tiingo and Alpha Vantage.
    Annualized Return Calculation: Analyze stock performance over time.
    Modular JAR Packaging: Library available as a JAR for integration into other applications.
    Enhanced Stability: Backup services and robust error handling for improved availability.
    Performance Optimizations: Multithreaded processing for increased efficiency.

🤝 Contributing

We welcome contributions! Fork the repository and submit a pull request to add new features or improve the existing code.
📄 License

This project is licensed under the Apache License 2.0.
📞 Contact

For questions or feedback, feel free to reach out:

    GitHub: govindnathsng
    Email: govindnathsng@gmail.com

Check out my <a href="https://www.crio.do/learn/portfolio/govindnathsng/" target="_blank">Crio Portfolio</a> for more projects like this!

yaml


---

This README highlights key features, technologies, and setup instructions, with a link to your Crio portfolio. Let me know if there’s anything else you’d like to add!
