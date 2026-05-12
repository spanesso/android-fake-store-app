# ─── core:database consumer rules ────────────────────────────────────────────
# QUÉ NO SE OFUSCA y POR QUÉ:
#   Clases @Entity — Room KSP genera código que accede a los campos y al nombre
#   de tabla (tableName). Si la clase se renombra, las migraciones de schema que
#   comparan nombres de tabla fallan. También afecta a Room.databaseBuilder con
#   el esquema exportado.
#   Clases @Dao — Room genera implementaciones que extienden las interfaces DAO
#   por nombre. Si el DAO se renombra, la implementación generada no compila.
#   @TypeConverter — Room registra los conversores por clase concreta.

-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.TypeConverter class * { *; }
-keepclassmembers @androidx.room.Entity class * { *; }
-keepclassmembers @androidx.room.Dao interface * { *; }

-dontwarn androidx.room.**
