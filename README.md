# 🧠 Analizador Léxico JSON con Indentación Jerárquica

Este proyecto implementa un **analizador léxico de archivos JSON**, el cual genera una salida jerárquica de los tokens reconocidos. La indentación en la salida refleja la estructura del archivo JSON.

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Java  
- **IDE:** NetBeans (utilizado para el desarrollo)  
- **Herramientas:** Terminal para compilación y ejecución

---

## 📋 Requerimientos

- Java JDK 8 o superior  
- Terminal (o consola de comandos)  
- Archivos de entrada en formato JSON válido  

---

## 📁 Archivos de Entrada

El proyecto incluye algunos archivos de ejemplo:

- `fuente.txt`
- `ejemplo.txt`
- `ejemplo2.txt`

Podés usar cualquiera de ellos o crear tu propio archivo JSON para analizar.

---

## ▶️ Instrucciones de Uso

### 1. Clonar el Repositorio

```bash
git clone https://github.com/Naidess/TP01_compiladores.git
```

### 2. Compilar el Proyecto

```bash
javac -d build/classes src/analizadorlexico/*.java
```

### 3. Ejecutar el Analizador

```bash
java -cp build/classes analizadorlexico.AnalizadorLexico fuente.txt > salida.txt
```

**Parámetros:**
- `fuente.txt`: nombre del archivo JSON a analizar (podés usar `fuente.txt`, `ejemplo.txt`, `ejemplo2.txt`, etc.)
- `salida.txt`: archivo de salida donde se generará la estructura jerárquica de tokens.

**Nota:** Si la carpeta `build/classes` no existe, créala manualmente o ejecuta primero el comando de compilación.