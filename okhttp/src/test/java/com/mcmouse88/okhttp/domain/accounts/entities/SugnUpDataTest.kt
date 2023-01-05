package com.mcmouse88.okhttp.domain.accounts.entities

import com.mcmouse88.okhttp.domain.EmptyFieldException
import com.mcmouse88.okhttp.domain.Field
import com.mcmouse88.okhttp.domain.PasswordMismatchException
import com.mcmouse88.okhttp.test_utils.catch
import com.mcmouse88.okhttp.test_utils.createSignUpData
import com.mcmouse88.okhttp.test_utils.wellDone
import org.junit.Assert
import org.junit.Test

class SignUpDataTest {

    /**
     * В данных тестах проверяем работоспособность валидации полей при вводе данных, если
     * какое-либо поле не заполнено, то выбрасывается исключение [EmptyFieldException]. Пятый по
     * счету тест проверяет валидность полей и что не произошло никаких исключений.
     */
    @Test
    fun validateForBlankEmailThrowsException() {
        val signUpData = createSignUpData(email = "      ")
        val exception: EmptyFieldException = catch { signUpData.validate() }
        Assert.assertEquals(Field.Email, exception.field)
    }

    @Test
    fun validateForBlankUsernameThrowsException() {
        val signUpData = createSignUpData(username = "      ")
        val exception: EmptyFieldException = catch { signUpData.validate() }
        Assert.assertEquals(Field.Username, exception.field)
    }

    @Test
    fun validateForBlankPasswordThrowsException() {
        val signUpData = createSignUpData(password = "     ")
        val exception: EmptyFieldException = catch { signUpData.validate() }
        Assert.assertEquals(Field.Password, exception.field)
    }

    @Test
    fun validateForMismatchedPasswordsThrowsExceptions() {
        val signUpData = createSignUpData(
            password = "password1",
            repeatPassword = "password2"
        )

        catch<PasswordMismatchException> { signUpData.validate() }
        wellDone()
    }

    @Test
    fun validateForValidDataDoesNothing() {
        val signUpData = createSignUpData()
        signUpData.validate()
        wellDone()
    }
}