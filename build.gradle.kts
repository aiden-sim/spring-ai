plugins {
	kotlin("jvm") apply false
	id("org.springframework.boot") version "3.5.10" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
	kotlin("plugin.spring") version "2.2.20"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

extra["springAiVersion"] = "1.1.2"

subprojects {
	group = rootProject.group
	version = rootProject.version

	repositories {
		mavenCentral()
	}

	afterEvaluate {
		if (plugins.hasPlugin("io.spring.dependency-management")) {
			the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
				imports {
					mavenBom("org.springframework.ai:spring-ai-bom:${rootProject.extra["springAiVersion"]}")
				}
			}
		}

		if (plugins.hasPlugin("java")) {
			extensions.configure<JavaPluginExtension> {
				toolchain {
					languageVersion.set(JavaLanguageVersion.of(21))
				}
			}

			dependencies {
				"implementation"("com.fasterxml.jackson.module:jackson-module-kotlin")
				"implementation"("org.jetbrains.kotlin:kotlin-reflect")
				"implementation"(kotlin("stdlib-jdk8"))
				"testImplementation"("org.springframework.boot:spring-boot-starter-test")
				"testImplementation"("org.jetbrains.kotlin:kotlin-test-junit5")
				"testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
			}

			tasks.withType<Test> {
				useJUnitPlatform()
			}
		}
	}
}
