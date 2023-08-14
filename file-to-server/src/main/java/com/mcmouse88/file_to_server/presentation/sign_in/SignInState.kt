package com.mcmouse88.file_to_server.presentation.sign_in

sealed interface SignInState {
    object Loading : SignInState
    object NotLoggedIn : SignInState
    class Error(val message: String) : SignInState
}