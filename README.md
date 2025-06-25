# TurboMart 🛒

**TurboMart** is a modern, feature-rich Android application for last-minute grocery shopping, inspired by leading quick-commerce platforms. It offers a seamless user experience for browsing, searching, and ordering groceries, with real-time order tracking and integrated digital payments.

---

## 🚀 Features

- **User Authentication**: Secure OTP-based phone number login.
- **Product Catalog**: Browse a wide range of grocery categories (Fruits & Vegetables, Dairy, Snacks, Beverages, Personal Care, and more).
- **Search & Filter**: Quickly find products with advanced search and category filters.
- **Cart Management**: Add, update, or remove products from your cart with real-time price calculation.
- **Order Placement**: Place orders with address management and order summary.
- **Digital Payments**: Integrated with Razorpay and PhonePe for secure UPI and card payments.
- **Order Tracking**: View your order history and track current orders.
- **Push Notifications**: Get real-time updates on order status and promotions.
- **Persistent Storage**: Cart and user data are stored locally using Room DB and synced with Firebase.
- **Modern UI/UX**: Clean, responsive, and visually appealing interface with Lottie animations and shimmer effects.

---

## 🏗️ Tech Stack

- **Kotlin** & **Android Jetpack** (ViewModel, LiveData, Navigation)
- **Firebase** (Authentication, Realtime Database, Cloud Messaging)
- **Room Database** for local persistence
- **Retrofit** for API calls
- **Razorpay** & **PhonePe** SDKs for payments
- **Lottie** for animations, **Glide** for image loading
- **Material Components** for UI

---

## 📲 User Flow

1. **Splash Screen**: App checks for existing user session.
2. **Sign In**: User enters phone number, receives OTP, and logs in.
3. **Home**: Browse featured products, categories, and best sellers.
4. **Search**: Find products by name or filter by category.
5. **Product Details**: View product info, add to cart, adjust quantity.
6. **Cart**: Review selected items, update quantities, proceed to checkout.
7. **Address**: Enter or select delivery address.
8. **Payment**: Pay securely via Razorpay or PhonePe.
9. **Order Confirmation**: View order summary and track status.
10. **Profile**: Manage address, view order history, and log out.

---

## 💳 Payment Integration

- **Razorpay**: For card, UPI, and wallet payments.
- **PhonePe**: UPI payments via PhonePe Intent SDK.
- **Order status** is updated in real-time after payment confirmation.

---

## 📦 Data Model

- **Products**: Title, price, quantity, category, images, stock.
- **Cart**: Local Room DB for offline persistence.
- **Orders**: Linked to user, includes items, address, status, and date.
- **Users**: Phone number, address, FCM token.

---

## 🔔 Notifications

- **Order updates** and promotional messages are sent via Firebase Cloud Messaging.
- Custom notification service for rich notifications.

---

## 🛠️ Setup & Installation

1. **Clone the repository**  
   ```bash
   git clone <your-repo-url>
   ```

2. **Open in Android Studio**  
   - Sync Gradle and let dependencies install.

3. **Firebase Setup**  
   - Add your `google-services.json` to `app/`.

4. **Razorpay & PhonePe**  
   - Register and obtain API keys.
   - Replace test keys in the code with your production keys.

5. **Run the app**  
   - Build and deploy on an Android device (minSdk 26+).

---

## 📸 Screenshots

> _Add screenshots of Home, Product, Cart, Payment, and Order Tracking screens here for maximum impact!_

---

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

---

## 📄 License

This project is for educational/demo purposes. For commercial use, please contact the author.

---

## 🙌 Credits

- Inspired by Blinkit, Zepto, and other quick-commerce apps.
- Built with ❤️ using open-source libraries and APIs.

---

**TurboMart** – _Your last-minute grocery solution!_ 