# Currency Converter
**Currency Converter** is application which fetches the realtime currency values from API and converts the desired currency of the user.

<img src="https://github.com/AdityaShidlyali/CurrencyConverterApp/blob/main/images/currency_converter.jpg" />

## Features
* User can enter the desired conversion fromats.
* Conversion can be done accross 170+ currencies as specified by the API.
* Material design guidelines followed to increase the user experience.

## Project structure (MVVM)
* di
* helper
* models
* network
* repositories
* view
* viewmodels

<img src="https://github.com/AdityaShidlyali/CurrencyConverterApp/blob/main/images/mvvm.png" />

## Android :heart: Koltin
* Retrofit is used with ScalarConverters to get the JSON response as the string.
* Default repository pattern is used for effective error handling and ease of testing by providing mock repositories.
* Hilt framework is used for dependency injection.

## Tech stack used
- [Retrofit 2](https://square.github.io/retrofit/) - REST client for making network calls.
- [Coroutines](https://developer.android.com/kotlin/coroutines) - For asynchronous operations.
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Lifecycle aware library to manage data observing the lifecycle of licecycle owner.
- [Hilt-Dagger](https://dagger.dev/hilt/) - Recommended Dependency Injection Framework for Android.
- [Kotlin Flows](https://developer.android.com/kotlin/flow) - Emits sequence of values or data, and consumer asynchronously consumes these values or data.
