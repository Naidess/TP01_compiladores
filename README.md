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
git clone https://github.com/tuusuario/analizador-lexico-json.git
cd analizador-lexico-json
```

### 2. Clonar el Repositorio

```bash
javac -d . src/analizadorlexico/*.java
```
### 3. Ejecutar el Analizador

```bash
java analizadorlexico.AnalizadorLexico fuente.txt > salida.txt
```
- `fuente.txt`: nombre del archivo JSON a analizar (podés usar fuente.txt, ejemplo.txt, etc.)
- `salida.txt`: archivo de salida donde se generará la estructura jerárquica de tokens.