import com.blamejared.ambientenvironment.gradle.Versions
import com.blamejared.ambientenvironment.gradle.Properties

plugins {
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
    id("com.blamejared.ambientenvironment.default")
}

minecraft {
    version(Versions.MINECRAFT)
    runs {
        client("Common Client") {
            workingDirectory(project.file("run"))
        }
    }
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")
}