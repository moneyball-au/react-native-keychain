package com.oblador.keychain;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS;

/**
 * @see <a href="https://stackoverflow.com/questions/50968732/determine-if-biometric-hardware-is-present-and-the-user-has-enrolled-biometrics">Biometric hradware</a>
 */
@SuppressWarnings({"WeakerAccess", "deprecation"})
public class DeviceAvailability {
  public static boolean isStrongBiometricAuthAvailable(@NonNull final Context context) {
    return BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BIOMETRIC_SUCCESS;
  }

  public static boolean isFingerprintAuthAvailable(@NonNull final Context context) {
    return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
  }

  public static boolean isFaceAuthAvailable(@NonNull final Context context) {
    return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FACE);
  }

    public static boolean isIrisAuthAvailable(@NonNull final Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_IRIS);
    }

  public static boolean isDeviceSecure(@NonNull final Context context) {
    final KeyguardManager km =
      (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return km.isDeviceSecure();
    } else {
      // NOTE: `KeyguardManager#isKeyguardSecure()` considers SIM locked state,
      // but it will be ignored on falling-back to device credential on biometric authentication.
      // That means, setting level to `SECURITY_LEVEL_SECRET` might be misleading for some users.
      // But there is no equivalent APIs prior to M.
      // `andriodx.biometric.BiometricManager#canAuthenticate(int)` looks like an alternative,
      // but specifying `BiometricManager.Authenticators.DEVICE_CREDENTIAL` alone is not
      // supported prior to API 30.
      // https://developer.android.com/reference/androidx/biometric/BiometricManager#canAuthenticate(int)
      return km.isKeyguardSecure();
    }
  }

  /** Check is permissions granted for biometric things. */
  public static boolean isPermissionsGranted(@NonNull final Context context) {
    // before api23 no permissions for biometric, no hardware == no permissions
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return false;
    }

    final KeyguardManager km =
      (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
    if( !km.isKeyguardSecure() ) return false;

    // api28+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      return context.checkSelfPermission(Manifest.permission.USE_BIOMETRIC) == PERMISSION_GRANTED;
    }

    // before api28
    return context.checkSelfPermission(Manifest.permission.USE_FINGERPRINT) == PERMISSION_GRANTED;
  }
}
