import java.util.Scanner;

/**
 * Programa de ordenamiento y búsqueda sobre un arreglo estático de palabras.
 *
 * El usuario define el tamaño del arreglo y lo llena con Strings. Luego puede
 * ordenarlo con selección, inserción, burbuja, mezcla (merge sort) o rápido
 * (quick sort) y, una vez ordenado, buscar una palabra con búsqueda binaria.
 *
 * Todas las comparaciones ignoran mayúsculas/minúsculas (compareToIgnoreCase),
 * de modo que el orden y la búsqueda binaria usan siempre el mismo criterio.
 */
public class OrdenamientoBusqueda {
    static Scanner scanner = new Scanner(System.in);

    static String[] original;   // palabras tal como las escribió el usuario
    static String[] palabras;   // arreglo de trabajo (el que se ordena)
    static boolean ordenado = false;

    public static void main(String[] args) {
        llenarArreglo();
        menu();
    }

    // =====================================================================
    //  MENÚ Y ENTRADA DE DATOS
    // =====================================================================

    /** Pide el tamaño al usuario y llena el arreglo estático de palabras. */
    static void llenarArreglo() {
        int tamano = 0;
        while (tamano <= 0) {
            tamano = leerEntero("Cuantas palabras desea ingresar? ");
            if (tamano <= 0) {
                System.out.println("El tamano debe ser mayor que cero.");
            }
        }
        original = new String[tamano];
        for (int i = 0; i < tamano; i++) {
            String palabra = "";
            while (palabra.isEmpty()) {
                palabra = leerTexto("Palabra " + (i + 1) + ": ");
                if (palabra.isEmpty()) {
                    System.out.println("La palabra no puede estar vacia.");
                }
            }
            original[i] = palabra;
        }
        palabras = original.clone();
        ordenado = false;
        System.out.println("Arreglo lleno.");
    }

    static void menu() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println();
            System.out.println("===== ORDENAMIENTO Y BUSQUEDA =====");
            System.out.println("Ordenar con:");
            System.out.println("1. Seleccion");
            System.out.println("2. Insercion");
            System.out.println("3. Burbuja");
            System.out.println("4. Mezcla (merge sort)");
            System.out.println("5. Rapido (quick sort)");
            System.out.println("6. Busqueda binaria");
            System.out.println("7. Mostrar arreglo");
            System.out.println("8. Ingresar nuevas palabras");
            System.out.println("0. Salir");
            opcion = leerEntero("Opcion: ");
            System.out.println();
            ejecutar(opcion);
        }
    }

    static void ejecutar(int opcion) {
        switch (opcion) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                ordenarConAlgoritmo(opcion);
                break;
            case 6:
                buscarPalabra();
                break;
            case 7:
                mostrarArreglo();
                break;
            case 8:
                llenarArreglo();
                break;
            case 0:
                System.out.println("Hasta luego.");
                break;
            default:
                System.out.println("Opcion invalida.");
        }
    }

    /**
     * Parte siempre del arreglo original (así se pueden comparar algoritmos
     * sobre los mismos datos), aplica el algoritmo elegido y muestra el resultado.
     */
    static void ordenarConAlgoritmo(int opcion) {
        palabras = original.clone();
        switch (opcion) {
            case 1:
                ordenamientoSeleccion(palabras);
                System.out.println("Ordenado por seleccion. Complejidad: O(n^2).");
                break;
            case 2:
                ordenamientoInsercion(palabras);
                System.out.println("Ordenado por insercion. Complejidad: O(n^2).");
                break;
            case 3:
                ordenamientoBurbuja(palabras);
                System.out.println("Ordenado por burbuja. Complejidad: O(n^2).");
                break;
            case 4:
                ordenarPorMezcla(palabras, 0, palabras.length - 1);
                System.out.println("Ordenado por mezcla. Complejidad: O(n log n).");
                break;
            case 5:
                ordenarRapido(palabras, 0, palabras.length - 1);
                System.out.println("Ordenado por rapido. Complejidad: O(n log n) promedio, O(n^2) peor caso.");
                break;
        }
        ordenado = true;
        mostrarArreglo();
    }

    static void mostrarArreglo() {
        System.out.print("Arreglo" + (ordenado ? " (ordenado)" : " (sin ordenar)") + ": [");
        for (int i = 0; i < palabras.length; i++) {
            System.out.print(palabras[i]);
            if (i < palabras.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    /** Pide una palabra y la busca con búsqueda binaria (solo si ya está ordenado). */
    static void buscarPalabra() {
        if (!ordenado) {
            System.out.println("Primero ordene el arreglo (opciones 1 a 5): la busqueda binaria lo requiere.");
            return;
        }
        String buscada = leerTexto("Palabra a buscar: ");
        int posicion = busquedaBinaria(palabras, buscada);
        if (posicion == -1) {
            System.out.println("La palabra \"" + buscada + "\" no esta en el arreglo.");
        } else {
            System.out.println("La palabra \"" + buscada + "\" esta en la posicion " + posicion
                    + " (indice base 0).");
        }
    }

    // =====================================================================
    //  ALGORITMOS DE ORDENAMIENTO (subrutinas)
    // =====================================================================

    /**
     * ORDENAMIENTO POR SELECCIÓN (algoritmo directo)
     * Complejidad de tiempo: O(n^2) en el peor caso y en el caso esperado.
     * Siempre hace n-1 pasadas y, en total, n(n-1)/2 = (n^2 - n)/2 comparaciones;
     * al ignorar la constante y el término no dominante queda O(n^2).
     * Complejidad de espacio: O(1), solo usa variables de control.
     */
    public static void ordenamientoSeleccion(String[] palabras) {
        //Se declaran algunas variables de control necesarias.
        int n = palabras.length;
        int menor;
        String temp;

        //El ciclo externo se usa para recorrer todo el arreglo (n - 1 pasadas).
        for (int i = 0; i < n - 1; i++) {
            //Se supone que el menor de la porción restante está en la casilla considerada.
            menor = i;

            //El ciclo interno recorre la porción restante, omitiendo las casillas ya ordenadas, para buscar el dato mínimo.
            for (int j = i + 1; j < n; j++) {
                if (palabras[j].compareToIgnoreCase(palabras[menor]) < 0) {
                    menor = j;
                }
            }

            //Se sustituye la casilla considerada en esta pasada por el valor mínimo encontrado.
            temp = palabras[i];
            palabras[i] = palabras[menor];
            palabras[menor] = temp;
        }
    }

    /**
     * ORDENAMIENTO POR INSERCIÓN (algoritmo directo)
     * Se construye un arreglo nuevo, insertando en cada pasada una palabra del
     * original en su lugar correspondiente, y al final se sustituye el original.
     * Complejidad de tiempo: O(n^2) en el peor caso y en el caso esperado:
     * por cada una de las n palabras se recorre y se desplaza parte del arreglo nuevo.
     * Complejidad de espacio: O(n), por el arreglo nuevo del mismo tamaño que el original.
     */
    public static void ordenamientoInsercion(String[] palabras) {
        //Se crea un nuevo arreglo del mismo tamaño que el original y se inicializan variables de control.
        int n = palabras.length;
        String[] palabrasNuevo = new String[n];
        int posicionInsertar;
        int cantidadInsertados = 0;

        //El ciclo externo se usa para recorrer todo el arreglo original.
        for (int i = 0; i < n; i++) {
            //Por defecto, la palabra va al final de las ya insertadas.
            posicionInsertar = cantidadInsertados;

            //Este primer ciclo interno determina la casilla del arreglo nuevo en la que se debe insertar la siguiente palabra:
            //la primera casilla (entre las ya insertadas) cuya palabra no sea menor que la que se va a insertar.
            for (int j = 0; j < cantidadInsertados; j++) {
                if (palabras[i].compareToIgnoreCase(palabrasNuevo[j]) <= 0) {
                    posicionInsertar = j;
                    break;
                }
            }

            //Este segundo ciclo interno desplaza hacia adelante, de las casillas mayores a las menores (para no
            //sobrescribir datos antes de tiempo), todas las palabras mayores que la que se va a insertar.
            for (int k = cantidadInsertados; k > posicionInsertar; k--) {
                palabrasNuevo[k] = palabrasNuevo[k - 1];
            }

            //Se inserta la palabra nueva.
            palabrasNuevo[posicionInsertar] = palabras[i];

            //Se actualiza la variable de control tras cada iteración del ciclo externo.
            cantidadInsertados++;
        }

        //Se sustituye cada casilla del arreglo original por la correspondiente casilla del arreglo nuevo.
        for (int c = 0; c < n; c++) {
            palabras[c] = palabrasNuevo[c];
        }
    }

    /**
     * ORDENAMIENTO POR BURBUJA (algoritmo directo)
     * Va acomodando primero las palabras mayores, al final del arreglo; las menores
     * van "subiendo" poco a poco hacia el inicio.
     * Complejidad de tiempo: O(n^2) en el peor caso y en el caso esperado:
     * n-1 pasadas, y en cada una se compara una porción de hasta n-1 pares.
     * Complejidad de espacio: O(1).
     */
    public static void ordenamientoBurbuja(String[] palabras) {
        //Se declaran algunas variables de control necesarias.
        int n = palabras.length;
        String temp;

        //El ciclo externo cuenta las pasadas (n - 1); cada una deja una casilla más ordenada al final.
        for (int i = 0; i < n - 1; i++) {
            //El ciclo interno recorre la porción por considerar, que es una casilla menor en cada pasada.
            for (int j = 0; j < n - 1 - i; j++) {
                //Se compara cada par de vecinos y, si están desordenados, se corrige antes de pasar a la siguiente casilla.
                if (palabras[j].compareToIgnoreCase(palabras[j + 1]) > 0) {
                    temp = palabras[j];
                    palabras[j] = palabras[j + 1];
                    palabras[j + 1] = temp;
                }
            }
        }
    }

    /**
     * ORDENAMIENTO POR MEZCLA (MERGE SORT) - "divide y vencerás"
     * (Algoritmo avanzado: las presentaciones solo lo mencionan; la consigna pide
     * investigarlo, por eso esta es la versión estándar recursiva.)
     * Complejidad de tiempo: O(n log n) en el peor caso y en el caso esperado: el
     * arreglo se divide en mitades log n veces y cada nivel de mezcla cuesta O(n).
     * Complejidad de espacio: O(n), por el arreglo auxiliar de la mezcla.
     */
    static void ordenarPorMezcla(String[] a, int inicio, int fin) {
        // Caso base: una porción de 0 o 1 elemento ya está ordenada.
        if (inicio >= fin) {
            return;
        }
        // Etapa 1 (dividir): se calcula el punto medio.
        int medio = (inicio + fin) / 2;
        // Etapa 2 (conquistar): se ordena cada mitad recursivamente.
        ordenarPorMezcla(a, inicio, medio);
        ordenarPorMezcla(a, medio + 1, fin);
        // Etapa 3 (combinar): se mezclan las dos mitades ya ordenadas.
        mezclar(a, inicio, medio, fin);
    }

    /** Mezcla las porciones ordenadas [inicio..medio] y [medio+1..fin]. */
    static void mezclar(String[] a, int inicio, int medio, int fin) {
        // Se copia la porción a un arreglo auxiliar para poder sobrescribir "a".
        String[] auxiliar = new String[fin - inicio + 1];
        int izq = inicio;       // recorre la mitad izquierda
        int der = medio + 1;    // recorre la mitad derecha
        int k = 0;              // posición en el auxiliar

        // Mientras ambas mitades tengan elementos, se copia siempre el menor.
        // Se usa <= para que, en un empate, salga primero el de la izquierda
        // (así el algoritmo es estable).
        while (izq <= medio && der <= fin) {
            if (a[izq].compareToIgnoreCase(a[der]) <= 0) {
                auxiliar[k++] = a[izq++];
            } else {
                auxiliar[k++] = a[der++];
            }
        }
        // Cuando una mitad se agota, se copia lo que quede de la otra.
        while (izq <= medio) {
            auxiliar[k++] = a[izq++];
        }
        while (der <= fin) {
            auxiliar[k++] = a[der++];
        }
        // Se devuelve el resultado ordenado al arreglo original.
        for (k = 0; k < auxiliar.length; k++) {
            a[inicio + k] = auxiliar[k];
        }
    }

    /**
     * ORDENAMIENTO RÁPIDO (QUICK SORT) - "divide y vencerás"
     * (Algoritmo avanzado: las presentaciones solo lo mencionan; la consigna pide
     * investigarlo, por eso esta es la versión estándar recursiva.)
     * Complejidad de tiempo: O(n log n) en el caso esperado; O(n^2) en el peor caso,
     * cuando el pivote divide muy mal (siempre el menor o el mayor). Elegir el
     * pivote del medio evita ese peor caso en arreglos ya ordenados.
     * Complejidad de espacio: O(log n) esperado, solo por la pila de la recursión.
     */
    static void ordenarRapido(String[] a, int inicio, int fin) {
        // Caso base: una porción de 0 o 1 elemento ya está ordenada.
        if (inicio >= fin) {
            return;
        }
        // Etapa 1: se particiona; el pivote queda en su posición definitiva.
        int posicionPivote = particionar(a, inicio, fin);
        // Etapa 2: se ordenan recursivamente los menores y los mayores al pivote.
        ordenarRapido(a, inicio, posicionPivote - 1);
        ordenarRapido(a, posicionPivote + 1, fin);
    }

    /** Coloca los menores al pivote a su izquierda y los mayores a su derecha. */
    static int particionar(String[] a, int inicio, int fin) {
        // Se elige como pivote el elemento del medio y se lleva al final.
        int medio = (inicio + fin) / 2;
        intercambiar(a, medio, fin);
        String pivote = a[fin];

        // "frontera" marca dónde termina el grupo de elementos menores al pivote.
        int frontera = inicio;
        for (int j = inicio; j < fin; j++) {
            // Cada elemento menor que el pivote se agrega al grupo de menores.
            if (a[j].compareToIgnoreCase(pivote) < 0) {
                intercambiar(a, frontera, j);
                frontera++;
            }
        }
        // Se coloca el pivote justo después del grupo de menores.
        intercambiar(a, frontera, fin);
        return frontera;
    }

    /** Intercambia los valores de dos casillas del arreglo. */
    static void intercambiar(String[] a, int i, int j) {
        String temporal = a[i];
        a[i] = a[j];
        a[j] = temporal;
    }

    // =====================================================================
    //  BÚSQUEDA BINARIA (subrutina)
    // =====================================================================

    /**
     * BÚSQUEDA BINARIA
     * Requisito: el arreglo debe estar ordenado.
     * Complejidad de tiempo: O(log n) en el peor caso y en el caso esperado. En cada
     * comparación se descarta la mitad de las casillas que quedan por revisar, así
     * que para n palabras se necesitan a lo sumo log2(n) comparaciones (con 1 000
     * palabras, unas 10; con 1 000 000, unas 20). Un caso especial es cuando la
     * palabra está justo en el centro: una sola comparación, O(1).
     * Complejidad de espacio: O(1), solo usa variables de control.
     *
     * @return el índice de la casilla donde está la palabra, o -1 si no se encontró.
     */
    public static int busquedaBinaria(String[] palabras, String valorBuscado) {
        //Se declaran e inicializan las variables de control necesarias.
        int limiteMenor = 0;
        int limiteMayor = palabras.length - 1;
        int pivote = (limiteMenor + limiteMayor) / 2;
        int comparaciones = 1;

        //El ciclo se usa para seguir reduciendo el intervalo de búsqueda mientras no se haya encontrado el valor buscado.
        while (palabras[pivote].compareToIgnoreCase(valorBuscado) != 0) {
            //Se ajusta alguno de los dos límites del intervalo en función de la comparación del pivote con el valor buscado.
            if (palabras[pivote].compareToIgnoreCase(valorBuscado) > 0) {
                limiteMayor = pivote - 1;   //el pivote es mayor: se descarta la mitad mayor
            } else {
                limiteMenor = pivote + 1;   //el pivote es menor: se descarta la mitad menor
            }

            //Si los límites se llegan a traslapar, el valor buscado no está en la estructura y se detiene la ejecución.
            if (limiteMenor > limiteMayor) {
                System.out.println("Comparaciones realizadas: " + comparaciones);
                return -1;
            }

            //Previamente a la siguiente iteración, se redefine el pivote en función de los límites actualizados.
            pivote = (limiteMayor + limiteMenor) / 2;
            comparaciones++;
        }

        System.out.println("Comparaciones realizadas: " + comparaciones);
        return pivote;
    }

    // =====================================================================
    //  LECTURA SEGURA DE DATOS
    // =====================================================================

    static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!scanner.hasNextLine()) {
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    static int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero entero valido.");
            }
        }
    }
}
