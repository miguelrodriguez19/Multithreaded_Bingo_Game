import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
/**
 * @author Miguel
 * @Bingo Juego del bingo con hilos 
 */


/**
 * Clase que sirve para crear cada uno de los hilos de los jugadores
 */
class Jugador extends Thread {
	private final int TOTAL_CARTON = 5; // Cantidad de numeros por carton
	private final int TOTAL_BOMBO; // Numeros posibles del bombo
	private int idJugador;
	private Set<Integer> carton; // Para almacenar los numeros pendientes de acertar
	private Bombo b;

	/**
	 * @param b
	 * @param identificador del jugador
	 */
	public Jugador(int idJugador, Bombo b) {
		this.idJugador = idJugador;
		this.b = b;
		this.TOTAL_BOMBO = b.getTOTAL_BOMBO();
		this.carton = new HashSet<Integer>();
		while (carton.size() < TOTAL_CARTON)
			carton.add((int) Math.floor(Math.random() * TOTAL_BOMBO) + 1);
	}

	/**
	 * Imprime los numeros pendientes del carton
	 */
	public synchronized void imprimeCarton() {
		String carton_jugador = "Carton jugador " + idJugador + ": ";

		for (Integer integer : this.carton)
			carton_jugador += integer + " ";

		System.out.println(carton_jugador);
	}

	/**
	 * Tacha el numero del carton en caso de que exista
	 * @param numero a tachar
	 */
	public void tacharNum(Integer numero) {
		carton.remove(numero);
	}

	public void run() {
		while (!b.isHayGanador()) {
			imprimeCarton();

			if (carton.size() == 0)
				b.setGanador(idJugador);

			tacharNum(b.getUltNumero());
		}
	}

}

/* ------------------------------------------------- CLASE PRESENTADOR ------------------------------------------------- */

/**
 * Clase que sirve para el hilo del presentador
 */
class Presentador extends Thread {
	private Bombo miBombo;

	public Presentador(Bombo bomb) {
		this.miBombo = bomb; // TODO Auto-generated constructor stub
	}

	public void run() {
		while (!miBombo.isHayGanador()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (!miBombo.isHayGanador()) {
				System.err.println("Ha salido el numero: " + miBombo.sacarNum());
				miBombo.imprimirBombo();
			}
		}
		if (!miBombo.isHayGanador()) {
			notifyAll();
		}
		System.err.println("¡¡Han Cantado Bingo!!  \nGanador jugador " + miBombo.getGanador());
		System.exit(0);
	}
}

/* ------------------------------------------------- CLASE BOMBO ------------------------------------------------- */

/**
 * Clase que se utiliza para crear el objeto compartido entre todos los hilos
 * del programa
 */
class Bombo {
	private final int TOTAL_BOMBO = 10; // Numeros posibles del bombo
	private Set<Integer> bombo; // Para almacenar los valores que van saliendo
	private volatile Integer ultNumero; // Ultimo numero del bombo
	private volatile boolean hayGanador;
	private int ganador;

	/**
	 * Inicializa vacio el bombo
	 */
	public Bombo() {
		bombo = new HashSet<Integer>();
		this.hayGanador = false;
	}

	/**
	 * @return El numero que sale del bombo
	 */
	public synchronized Integer sacarNum() {
		int bolita = 0, cantidadBolas = bombo.size();
		if (cantidadBolas < TOTAL_BOMBO) {
			do {
				ultNumero = (int) Math.floor(Math.random() * TOTAL_BOMBO) + 1;
				bombo.add(ultNumero);
				bolita = ultNumero;
			} while (cantidadBolas == bombo.size() && !hayGanador);
		} else
			System.out.println("Ya han salido todas las bolas");
		
		notifyAll(); // Se avisa a todos de que ya ha salido la bola

		return bolita; // Se envia la bola al presentador
	}
	
	/**
	 * Muestra todas las bolas que han salido hasta el momento
	 */
	public synchronized void imprimirBombo() {
		String contBombo = "-> Bolas sacadas hasta el momento: ";

		for (Integer integer : bombo)
			contBombo += integer + " ";
		
		System.out.println(contBombo);
	}
	
	/* ------------------ GETTERS Y SETTERS ------------------ */
	
	public int getTOTAL_BOMBO() {
		return TOTAL_BOMBO;
	}

	public boolean isHayGanador() {
		return hayGanador;
	}

	public void setHayGanador(boolean hayGanador) {
		this.hayGanador = hayGanador;
	}

	public int getGanador() {
		return ganador;
	}
	
	/**
	 * Settea el param como ganador y actualiza hayGanador a true
	 * @param ganador
	 */
	public synchronized void setGanador(int ganador) {
		this.ganador = ganador;
		this.hayGanador = true;
	}

	/**
	 * Frena a todos los jugadores hasta que haya salido la ultima bola y despues la devuelve
	 * @return
	 */
	public synchronized Integer getUltNumero() {
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return ultNumero;
	}

}

/* ------------------------------------------------- CLASE BINGO ------------------------------------------------- */

/**
 * Clase que inicia el Bingo
 */
public class Bingo {
	private int numPlayers;
	private Thread[] todosJugadores; 
	private static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		Bingo bingo = new Bingo();
		Bombo bomb = new Bombo();

		System.out.println("Bienvenidos al Bingo!");
		System.out.print("Indica el numero de jugadores: ");
		bingo.numPlayers = sc.nextInt();
		bingo.todosJugadores = new Jugador[bingo.numPlayers];

		// Se crea e inicia Presentador
		Presentador presentador = new Presentador(bomb);
		presentador.start();

		// Se crean e inician todos los jugadores
		for (int i = 0; i < bingo.todosJugadores.length; i++) {
			bingo.todosJugadores[i] = new Jugador(i + 1, bomb);
			bingo.todosJugadores[i].start();
		}

		// Se hace join de los jugadores y del presentador
		try {
			for (int i = 0; i < bingo.todosJugadores.length; i++)
				bingo.todosJugadores[i].join();

			presentador.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}