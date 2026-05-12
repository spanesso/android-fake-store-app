# ─── core:security consumer rules ────────────────────────────────────────────
# QUÉ NO SE OFUSCA y POR QUÉ:
#   Implementaciones de IntegrityChecker y BiometricAuthenticator — Hilt genera
#   fábricas (e.g., IntegrityCheckerImpl_Factory) que instancian la clase por
#   nombre concreto. Si R8 reempaqueta la clase en 'o.a', la fábrica generada
#   apunta al nombre original y el grafo de dependencias falla en runtime.
#   También preservamos IntegrityPolicy e IntegrityResult porque se usan en
#   AppModule mediante BuildConfig.INTEGRITY_POLICY y valueOf() que depende del
#   nombre del enum.

-keep class com.mango.fakestore.core.security.** implements * { *; }
-keep enum com.mango.fakestore.core.security.integrity.IntegrityPolicy { *; }
-keep class com.mango.fakestore.core.security.integrity.IntegrityResult { *; }

-dontwarn com.scottyab.rootbeer.**
