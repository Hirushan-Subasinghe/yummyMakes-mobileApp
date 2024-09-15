// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    //include google services dependencies - IM/2021/089 --start
    id("com.google.gms.google-services") version "4.4.2" apply false
    //include google services dependencies - IM/2021/089 --start
}