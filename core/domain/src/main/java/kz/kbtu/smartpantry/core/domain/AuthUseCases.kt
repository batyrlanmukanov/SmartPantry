package kz.kbtu.smartpantry.core.domain

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kz.kbtu.smartpantry.core.model.UserProfile
import kz.kbtu.smartpantry.core.model.UserSession

class ObserveSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<UserSession?> = authRepository.observeSession()
}

class CompleteSignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ) {
        val session = authRepository.signIn(
            email = email,
            password = password,
        )
        profileRepository.saveProfile(
            UserProfile(
                displayName = session.displayName,
                email = session.email,
            ),
        )
    }
}

class CompleteSignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(
        displayName: String,
        email: String,
        password: String,
    ) {
        val session = authRepository.signUp(
            displayName = displayName,
            email = email,
            password = password,
        )
        profileRepository.saveProfile(
            UserProfile(
                displayName = session.displayName,
                email = session.email,
            ),
        )
    }
}

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke() = authRepository.signOut()
}
