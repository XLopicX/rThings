# Retarded Things (rThings)

rThings conté petites (i horribles) llibreries que serveixen per a múltiples propòsits. Actualment n'hi ha 2.

## rDotEnv

Aquesta llibreria permet utilitzar arxius per a carregar informació al runtime del programa. Per exemple, si tens uns credencials a una base de dades és ideal no posar-los tal qual al codi (lol), i per això es solen fer servir els arxius .env on tu poses per exemple:

```
host:example.com
port:1111
user:example
pass:example
```

> El primer text previ als dos punts (:) és la key i el contingut després dels dos punts és el valor de la key. 

Per a fer servir aquesta llibreria **primer de tot la inicialitzem amb la path** del arxiu que contindrà les keys i els valors.

```java
rDotEnv dotenv = new rDotEnv(".env");
```

> A aquest exemple l'arxiu està a l'arrel del projecte i té el nom .env, però pots posar el que vulguis; no ha de ser .env ni tampoc te perquè estar a l'arrel. Una vegada inicialitzat, rDotEnv guarda tots els valors a un HashMap per a no haver d'estar comprovant l'arxiu cada vegada que vulguem obtenir algun valor. 

**Per accedir a un valor fem servir el següent codi:**

```java
dotenv.get("nom de la key")
```

## rJDBC

rJDBC és un wrapper per a JDBC que busca simplificar la implementació d'aquesta llibreria a un programa. rJDBC permet interactuar amb la base de dades generant les sentències mitjançant codi Java sense crear les sentències amb sintaxi SQL.

Per fer servir aquesta llibreria primer de tot necessitem la info per a connectar-nos a la base de dades d'Oracle:
* Host
* Port
* SN (Service name) - Si fas servir SID mala sort 😊
* Usuari
* Contrasenya

> Idealment fes servir una llibreria com rDotEnv per a no hard codejar les credencials.

Una vegada tinguem tot això inicialitzem la llibreria amb:

```java
rJDBC db = new rJDBC(dotenv.get("host"), Integer.parseInt(dotenv.get("port")), dotenv.get("sn"), dotenv.get("user"), dotenv.get("pass"));
```

Ara per connectar-nos a la base de dades fem:

```java
db.connect();
```

> Si no ha explotat res, felicitats, ja pots fer servir la llibreria! 

Aquí alguns exemples d'ús (Molta mandra de fer una bona documentació, pots llegir el codi i veure què més es pot fer ;D):

### Select

```java
String sentenciaSel = new rJDBC.SQLSelectBuilder()
                .select("FIRST_NAME", "LAST_NAME")
                .from("EMPLOYEES")
                .where("LAST_NAME LIKE 'R%'")
                .order("FIRST_NAME DESC")
                .build();
ResultSet firstExample = db.select(sentenciaSel);
```

> Igualment has de tractar manualment el [ResultSet](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html)

### Update

```java
String sentenciaUpd = new rJDBC.SQLUpdateBuilder()
                .update("employees")
                .set("department_id", "20")
                .where("last_name = 'King'")
                .build();
db.update(sentenciaUpd);
```

### Delete

```java
String sentenciaDel = new rJDBC.SQLDeleteBuilder()
                .from("employees")
                .where("employee_id = 100")
                .build();
db.delete(sentenciaDel);
```

### Insert

```java
String sentenciaIns = new rJDBC.SQLInsertBuilder()
                .insert(
                        "employee_id", "100",
                        "first_name", "'Steven'",
                        "last_name", "'King'",
                        "email", "'SKING'",
                        "phone_number", "'515.123.4567'",
                        "hire_date", "'17/06/1987'",
                        "job_id", "'AD_PRES'",
                        "salary", "24000",
                        "commission_pct", "null",
                        "manager_id", "null",
                        "department_id", "90"
                )
                .into("employees")
                .build();
db.insert(sentenciaIns);
```

### ⚠️ Utilitzar això a producció és mala idea, especialment rJDBC, ja que no està pensat com un projecte serio ni molt menys, no hi ha cap process de sanitització i no faig servir pooling ni res per a gestionar el obrir i tancar connexions (literalment s'obre a l'inici i es manté sempre oberta).
