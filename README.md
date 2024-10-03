# StarbuncleMania
Addon to the Ars Nouveau mod.
Similarly to Ars Creo, this mod aim to allow more magic/tech hybrids with the Ars style of using cute animal workers instead of techy pipes and cables. 

Every push to this repository is built and published to the [BlameJared](https://maven.blamejared.com) maven, to use
these builds in your project, simply add the following code in your build.gradle

```gradle
repositories {
    maven { url 'https://maven.blamejared.com' }
}

dependencies {
    implementation fg.deobf("com.alexthw.starbunclemania:starbunclemania-[MC_VERSION]:[VERSION]")
}
```

Current version (1.21.0 for file name, actually 1.21.1):
[![Maven](https://img.shields.io/maven-metadata/v?label=&color=C71A36&metadataUrl=https%3A%2F%2Fmaven.blamejared.com%2Fcom%2Falexthw%2Fstarbunclemania%2Fstarbunclemania-1.21.0%2Fmaven-metadata.xml&style=flat-square)](https://maven.blamejared.com/com/alexthw/starbunclemania/starbunclemania-1.21.0/)

(remove the v)