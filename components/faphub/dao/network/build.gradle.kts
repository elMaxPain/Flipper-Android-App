plugins {
    id("flipper.android-lib")
    id("flipper.ktorfit")
    id("com.squareup.anvil")
    id("kotlin-kapt")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.faphub.dao.network"

dependencies {
    implementation(projects.components.faphub.dao.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.data)
    implementation(projects.components.core.progress)
    implementation(projects.components.core.preference)

    implementation(projects.components.faphub.target.api)

    implementation(libs.kotlin.serialization.json)

    // Dagger deps
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.datetime)

    implementation(libs.annotations)
}
