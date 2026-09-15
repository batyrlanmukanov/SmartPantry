package kz.kbtu.smartpantry.core.domain

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kz.kbtu.smartpantry.core.model.UserProfile

class ObserveProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    operator fun invoke(): Flow<UserProfile> = profileRepository.observeProfile()
}

class SaveProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(profile: UserProfile) = profileRepository.saveProfile(profile)
}
