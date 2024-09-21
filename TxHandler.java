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
        UTXOPool currentPool = new UTXOPool(utxoPool); // this is the current UTXO pool
		HashSet<UTXO> utxoClaimed = new HashSet<>(); // created hash set to track for unclaimed UTXOs
		double inputSum = 0; // sum of input values
        double outputSum = 0; // sum of output values

		// Keep track of claimed UTXOs
        for (Transaction.Input inputTX : tx.getInputs()) { // for each input in tx
            UTXO utxoUnclaimed = new UTXO(inputTX.previousHash, inputTX.outputIndex); // create a UTXO object

			 // If current pool does not contain the UTXO, return false
			 if (!currentPool.contains(utxoUnclaimed)) {
				return false;
			}

			// Validate the signature on each input
			Transaction.Output output = currentPool.getTxOutput(utxoUnclaimed); // get the output of the UTXO of current pool
			// If the signature cannot be verified it is false
			if (!security.verifySignature(output.pkey, tx.getRawDataToSign(tx.getInputs().indexOf(inputTX)), inputTX.sig)) {
				return false;
			}
        }

        return false; // part of starter code, keep for now
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
