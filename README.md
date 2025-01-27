# ActivityStack

ActivityStack is a demonstration of how to create a card stack effect for activities on Android. This project is written entirely in Java.

## Features

- Card stack effect for activities
- Smooth animations
- Easy to integrate into existing projects

## Getting Started

### Prerequisites

- Android Studio
- Java Development Kit (JDK) 8 or higher
### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/bzk9x/ActivityStack.git
   ```
### Usage

1. Make sure the activity is transparent

```xml
<item name="android:windowIsTranslucent">true</item>  
<item name="android:windowBackground">@android:color/transparent</item>  
<item name="android:backgroundDimEnabled">true</item>  
<item name="android:backgroundDimAmount">0.4</item>
```

2. For slide in/out animation create `slide_in_bottom.xml` and `slide_out_bottom.xml` in your res/anim folder, and add the following:

```xml
<!--slide_in_bottom-->
<translate xmlns:android="http://schemas.android.com/apk/res/android"  
    android:fromYDelta="100%"  
    android:toYDelta="0%"  
    android:duration="350"  
    android:interpolator="@android:interpolator/decelerate_cubic" />
```

```xml
<!--slide_out_bottom-->
<translate xmlns:android="http://schemas.android.com/apk/res/android"  
    android:fromYDelta="0%"  
    android:toYDelta="100%"  
    android:duration="350"  
    android:interpolator="@android:interpolator/accelerate_cubic" />
```

Then you can use the animations by overriding the default animation

```java
// slide_in_bottom
overridePendingTransition(R.anim.slide_in_bottom, 0);
```

```java
// slide_out_bottom
overridePendingTransition(0, R.anim.slide_out_bottom);
```

