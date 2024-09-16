import java.util.HashSet;

public class TxHandler {

	/* Creates a public ledger whose current UTXOPool (collection of unspent 
	 * transaction outputs) is utxoPool. This should make a defensive copy of 
	 * utxoPool by using the UTXOPool(UTXOPool uPool) constructor.
	 */
	public TxHandler(UTXOPool utxoPool) {
		// IMPLEMENT THIS

		this.utxoPool = new UTXOPool(utxoPool); // create a defensive copy

		// Victor's Notes ---------------------------------------------------------
		/* TxHandler should only be 1 line so I think its done now./
		 * As for the rest of the code I am very confused. 
		 * After searching through all the files I don't see a "main" method anywhere so 
		 * I'm not sure how this code is going to compiile/execute even though this the only file 
		 * asking us to implenment sttuff.
		 * Help needed.
		 */
	}

	/* Returns true if 
	 * (1) all outputs claimed by tx are in the current UTXO pool, 
	 * (2) the signatures on each input of tx are valid, 
	 * (3) no UTXO is claimed multiple times by tx, 
	 * (4) all of tx’s output values are non-negative, and
	 * (5) the sum of tx’s input values is greater than or equal to the sum of   
	        its output values;
	   and false otherwise.
	 */

	public boolean isValidTx(Transaction tx) {
		// IMPLEMENT THIS

		// WIP not done yet -Victor -------------------------------------------------
        UTXOPool tempPool = new UTXOPool(utxoPool); // this is the current UTXO pool

		// Keep track of claimed UTXOs
        for (Transaction.Input TXinput : tx.getInputs()) { // for each input in tx
            UTXO utxoUnclaimed = new UTXO(TXinput.previousHash, TXinput.outputIndex); // create a UTXO object
        }

        return false; // part of starter code
    }

	/* Handles each epoch by receiving an unordered array of proposed 
	 * transactions, checking each transaction for correctness, 
	 * returning a mutually valid array of accepted transactions, 
	 * and updating the current UTXO pool as appropriate.
	 */
	public Transaction[] handleTxs(Transaction[] possibleTxs) {
		// IMPLEMENT THIS
		return null;
	}

} 
