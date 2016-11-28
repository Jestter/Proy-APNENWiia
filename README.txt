-----------------------
==Agente de Ajedrez==

 por Fabian Olivares 
 y Nicolas Pradenas
------------------------


Compilación: 
	Por línea de comandos ingresar a la carpeta src y ejecutar lo siguiente
	
	javac *.java

Ejecución:
	Por línea de comandos ingresar a la carpeta src y ejecutar lo siguiente

	java HotSingleAgent <1 (minimax)|2 (alphabeta prunning)> <archivo tbl> <tiempo máximo>

Notas:
- El agente HotSingleAgent.java es el utilizado para competencia y requiere del archivo TDNetwork.nn para cargar la red.
- La clase Server (src/Server.java) fue modificada para entrenamiento.
- La clase Trainer (en src) implementa el entremamiento utilizando TDLearning para la red neuronal.
- La clase NeuronalNetwork extiende la clase abstracta Heuristic y es utilizada en el agente.
- La clase Util.java probee de metodos de utilidad para la red neuronal y el entrenamiento.
