plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

architectury {
    common("neoforge", "fabric")
}

loom {
    silentMojangMappingsLicense()
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("com.cobblemon:mod:${property("cobblemon_version")}") { isTransitive = false }

    testImplementation("org.junit.jupiter:junit-jupiter-api:${property("junit_version")}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${property("junit_version")}")

    compileOnly("io.wispforest:owo-lib:${property("owo_version")}")
    annotationProcessor("io.wispforest:owo-lib:${property("owo_version")}")

    annotationProcessor("net.fabricmc:sponge-mixin:0.15.4+mixin.0.8.7")
    compileOnly("net.fabricmc:sponge-mixin:0.15.4+mixin.0.8.7")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}