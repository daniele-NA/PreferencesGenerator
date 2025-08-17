````markdown
# Lightweight Preferences Generator for Android

Easily define preferences with **less code** than standard Android libraries.  
Supports only **primitive types** (String included, Char excluded).  

✅ Compatible with **Java** and **Kotlin**.


````

## YOUR INTERFACE

```kotlin
import com.crescenzi.*;

@GeneratePreferences("my_preferences")
interface KotlinPref {

  @get:StringPref("Kevin")
    val name: String

    @get:IntPref(18)
    val age: Int

    @get:BooleanPref(true)
    val isActive: Boolean

    @get:LongPref(1000L)
    val timestamp: Long

    @get:FloatPref(12.5f)
    val priceFloat: Float

    @get:DoublePref(99.99)
    val priceDouble: Double

}
```

## GENERATED INTERFACE

```kotlin
// Various imports

class KotlinPrefImpl(val context: Context) {

    companion object {
        val NAME_KEY = stringPreferencesKey("name")
        val NAME_DEFAULT: String = "Kevin"
      
      //OTHERS PREFERENCES DECLARATIONS
    }

    private val Context.dataStore by preferencesDataStore(name = "my_preferences")
    private val prefs = context.dataStore
    private val scope = CoroutineScope(Dispatchers.IO)

  //OTHERS PREFERENCES FLOWS
  
    val name: StateFlow<String> = prefs.data
        .map { preferences -> preferences[NAME_KEY] ?: NAME_DEFAULT }
        .stateIn(scope, started = SharingStarted.Eagerly, initialValue = NAME_DEFAULT)

    suspend fun <T> updatePreference(key: Preferences.Key<T>, value: T) {
        prefs.edit { preferences -> preferences[key] = value }
    }

    suspend fun resetAllPreferences() {
        prefs.edit { preferences -> preferences[NAME_KEY] = NAME_DEFAULT }
      
        //OTHERS PREFERENCES RESET
    }
}
```

---

## Java Example

```java
@GeneratePreferences(preferencesName = "pref2")
interface JavaPref {

    @DoublePref(defaultValue = 12.0)
    double getPriceDouble();

    @FloatPref(defaultValue = 12f)
    float getPriceFloat();
}
```

## Import
```
// Make sure you have enabled Kapt In Top Plugins Section.... USE JAVA 17


dependencies {
  implementation("com.github.daniele-NA:PreferencesGenerator:v1.0.4")
  kapt("com.github.daniele-NA:PreferencesGenerator:v1.0.4") // Same Tag
}
```

## Usage


```kotlin
val prefs = KotlinPrefImpl(this) // Or via DI

prefs.updatePreference(KotlinPrefImpl.NAME_KEY, "Bob") // Update
prefs.resetAllPreferences() // Reset
prefs.name.collect { value -> println(value) } // Observe
```
---

## READ ABSOLUTELY

* The generated class **appends `Impl`** to your interface name.
  Example: `UserData` → `UserDataImpl`.
* The generated class **does not implement or extend** your interface.
* Do **not** declare multiple `@GeneratePreferences` with the same preferences name.
* Do **not** omit `get` before Java method declarations.
* Each preference comes with:

    * **Static key**
    * **Default value**
    * **Update method**
    * **Reset method**
    * **Observable state** (`StateFlow`)

---

## Contact

For questions or help: **[crescenziaziendale@gmail.com](mailto:crescenziaziendale@gmail.com)**
*Response time: <30 minutes*

```