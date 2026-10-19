package com.example.cloudstorage

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// ── Route constants ───────────────────────────────────────────────────────────

object Routes {
    const val SPLASH            = "splash"
    const val ONBOARDING        = "onboarding"
    const val SIGN_IN           = "sign_in"
    const val HOME              = "home"
    const val MY_FILES          = "my_files"
    const val UPLOAD_MANAGER    = "upload_manager"
    const val FILE_PREVIEW      = "file_preview"
    const val SHARED_FILES      = "shared_files"
    const val AUTO_BACKUP       = "auto_backup"
    const val OFFLINE_DOWNLOADS = "offline_downloads"
    const val PREMIUM           = "premium"
    const val PROFILE_INFORMATION = "profile_information"
    const val PROFILE           = "profile"
    const val LANGUAGE          = "language"
    const val HELP_SUPPORT      = "help_support"
    const val PRIVACY_POLICY    = "privacy_policy"
    const val ABOUT             = "about"
}

// ── Root navigation graph ─────────────────────────────────────────────────────

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // ── Splash ──────────────────────────────────────────────────────────
        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ── Onboarding ───────────────────────────────────────────────────────
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate(Routes.SIGN_IN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        // ── Sign In ──────────────────────────────────────────────────────────
        composable(Routes.SIGN_IN) {
            SignInScreen(
                onSignInSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SIGN_IN) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    // TODO: navigate to sign-up screen when ready
                }
            )
        }

        // ── Home Dashboard ───────────────────────────────────────────────────
        composable(Routes.HOME) {
            HomeDashboardScreen(
                onSignOut = {
                    navController.navigate(Routes.SIGN_IN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onNavigateToFiles = {
                    navController.navigate(Routes.MY_FILES)
                },
                onNavigateToUploadManager = {
                    navController.navigate(Routes.UPLOAD_MANAGER)
                },
                onNavigateToShared = {
                    navController.navigate(Routes.SHARED_FILES)
                },
                onNavigateToAutoBackup = {
                    navController.navigate(Routes.AUTO_BACKUP)
                },
                onNavigateToOffline = {
                    navController.navigate(Routes.OFFLINE_DOWNLOADS)
                },
                onNavigateToPremium = {
                    navController.navigate(Routes.PREMIUM)
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                }
            )
        }

        // ── My Files ─────────────────────────────────────────────────────────
        composable(Routes.MY_FILES) {
            MyFilesScreen(
                onFileClick = { file ->
                    if (!file.isFolder) {
                        navController.navigate(Routes.FILE_PREVIEW)
                    }
                },
                onBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onNavigateToShared = {
                    navController.navigate(Routes.SHARED_FILES)
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onNavigateToUploadManager = {
                    navController.navigate(Routes.UPLOAD_MANAGER)
                },
                onNavigateToAutoBackup = {
                    navController.navigate(Routes.AUTO_BACKUP)
                },
                onNavigateToOffline = {
                    navController.navigate(Routes.OFFLINE_DOWNLOADS)
                },
                onNavigateToPremium = {
                    navController.navigate(Routes.PREMIUM)
                }
            )
        }

        // ── Upload Manager ────────────────────────────────────────────────────
        composable(Routes.UPLOAD_MANAGER) {
            UploadManagerScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── File Preview ──────────────────────────────────────────────────────
        composable(Routes.FILE_PREVIEW) {
            FilePreviewScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── Shared Files ──────────────────────────────────────────────────────
        composable(Routes.SHARED_FILES) {
            SharedFilesScreen(
                onBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onNavigateToFiles = {
                    navController.navigate(Routes.MY_FILES)
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onNavigateToUploadManager = {
                    navController.navigate(Routes.UPLOAD_MANAGER)
                },
                onNavigateToAutoBackup = {
                    navController.navigate(Routes.AUTO_BACKUP)
                },
                onNavigateToOffline = {
                    navController.navigate(Routes.OFFLINE_DOWNLOADS)
                },
                onNavigateToPremium = {
                    navController.navigate(Routes.PREMIUM)
                }
            )
        }

        // ── Automatic Backup ──────────────────────────────────────────────────
        composable(Routes.AUTO_BACKUP) {
            AutomaticBackupScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── Offline Downloads ─────────────────────────────────────────────────
        composable(Routes.OFFLINE_DOWNLOADS) {
            OfflineDownloadsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── Premium ───────────────────────────────────────────────────────────
        composable(Routes.PREMIUM) {
            PremiumScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── Language ──────────────────────────────────────────────────────────
        composable(Routes.LANGUAGE) {
            val context = androidx.compose.ui.platform.LocalContext.current
            LanguageScreen(
                onBack = { navController.popBackStack() },
                onContinue = { selectedCode ->
                    LocalizationManager.setLanguage(context, selectedCode)
                    navController.popBackStack()
                }
            )
        }

        // ── Help & Support ────────────────────────────────────────────────────
        composable(Routes.HELP_SUPPORT) {
            HelpSupportScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── Security & Policy ─────────────────────────────────────────────────
        composable(Routes.PRIVACY_POLICY) {
            SecurityPolicyScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── About CloudBox ────────────────────────────────────────────────────
        composable(Routes.ABOUT) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── Profile ───────────────────────────────────────────────────────────
        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onNavigateToFiles = {
                    navController.navigate(Routes.MY_FILES)
                },
                onNavigateToShared = {
                    navController.navigate(Routes.SHARED_FILES)
                },
                onNavigateToUploadManager = {
                    navController.navigate(Routes.UPLOAD_MANAGER)
                },
                onNavigateToAutoBackup = {
                    navController.navigate(Routes.AUTO_BACKUP)
                },
                onNavigateToOffline = {
                    navController.navigate(Routes.OFFLINE_DOWNLOADS)
                },
                onNavigateToPremium = {
                    navController.navigate(Routes.PREMIUM)
                },
                onUpgrade = {
                    navController.navigate(Routes.PREMIUM)
                },
                onProfileInformationClick = {
                    navController.navigate(Routes.PROFILE_INFORMATION)
                },
                onSecurityPrivacyClick = {
                    navController.navigate(Routes.PRIVACY_POLICY)
                },
                onLanguageClick = {
                    navController.navigate(Routes.LANGUAGE)
                },
                onHelpSupportClick = {
                    navController.navigate(Routes.HELP_SUPPORT)
                },
                onAboutClick = {
                    navController.navigate(Routes.ABOUT)
                },
                onLogOut = {
                    navController.navigate(Routes.SIGN_IN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        // ── Profile Information ───────────────────────────────────────────────
        composable(Routes.PROFILE_INFORMATION) {
            ProfileInformationScreen(
                onBack = { navController.popBackStack() },
                onChangePhoto = {
                    // Handle photo change logic here
                },
                onSignOut = {
                    navController.navigate(Routes.SIGN_IN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
