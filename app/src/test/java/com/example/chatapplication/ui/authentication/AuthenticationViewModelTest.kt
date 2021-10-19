package com.example.chatapplication.ui.authentication

import com.example.chatapplication.models.Models
import com.example.chatapplication.other.MainCoroutineRule
import com.example.chatapplication.ui.authentication.mvi.AuthenticationState
import com.example.chatapplication.ui.authentication.mvi.AuthenticationStateEvent
import com.example.chatapplication.utils.Constants
import com.example.core.models.UserDomain
import com.example.core.usecases.authentication.LoginUserUseCase
import com.example.core.usecases.authentication.SignUpUserUseCase
import com.example.core.utils.DataState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class AuthenticationViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: AuthenticationViewModel

    @Mock
    lateinit var signUp: SignUpUserUseCase

    @Mock
    lateinit var login: LoginUserUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = AuthenticationViewModel(signUp, login)
    }

    @Test
    fun `login user with empty email returns false`() {
        viewModel.setIntent(AuthenticationStateEvent.LoginUser("", "password"))
        val result = viewModel.authState.value

        assertThat(result).isEqualTo(
            AuthenticationState(
                error = AuthenticationState.Error(Constants.FIELDS_NOT_FILLED_OUT_MSG),
                null
            )
        )
    }

    @Test
    fun `login user with empty password returns false`() {
        viewModel.setIntent(AuthenticationStateEvent.LoginUser("test@test.com", ""))
        val result = viewModel.authState.value

        assertThat(result).isEqualTo(
            AuthenticationState(
                error = AuthenticationState.Error(Constants.FIELDS_NOT_FILLED_OUT_MSG),
                null
            )
        )
    }

    @Test
    fun `login user with valid input returns true`() {
        val result = viewModel.dataState
        val expectedDataState = DataState.SUCCESS(UserDomain(1, "login"))

        runBlockingTest {
            Mockito.`when`(login.execute("login", "password")).thenReturn(flowOf(expectedDataState))
        }
        viewModel.setIntent(AuthenticationStateEvent.LoginUser("login", "password"))

        runBlockingTest {
            val collectJob = launch {
                result.collect {
                    assertThat(it).isEqualTo(expectedDataState)
                }
            }
            collectJob.cancel()
        }
    }

//    @Test
//    fun `sign up user with empty login returns false`() {
//
//    }
}