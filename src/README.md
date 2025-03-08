Home Solutions E-Commerce Platform

Author: Dominic Cash

Overview
Home Solutions is a lightweight, Java-based e-commerce web application designed to manage and sell home appliances. Built using the com.sun.net.httpserver package, it provides a simple server-side solution for browsing products, managing a shopping basket, and administering inventory. The application features a modern, consistent UI with a gradient background, Poppins font, and green buttons, styled uniformly across all pages.

This project demonstrates core web development concepts including HTTP request handling, SQLite database integration, session management with cookies, and basic CRUD operations (Create, Read, Update, Delete) for product management.

Usage

Home Page: Browse appliances at http://localhost:8080/.
Filter by category or price, search by description, or add items to the basket.

Shopping Basket: View at http://localhost:8080/basket/view.
Add items from the home page, clear the basket if needed.

Customers: View at http://localhost:8080/customers.

Admin Login: Access at http://localhost:8080/login.
Default is username: new_user and password: secure_password123. You can use the UserInserter class to insert more users.

Admin Panel: After login, manage products at http://localhost:8080/adminPanel.

Add new products (/addProduct), edit existing ones (/editProduct?id=X), or delete them (/deleteProduct?id=X).

