import java.util.HashSet;

public class TxHandler {

	/* Creates a public ledger whose current UTXOPool (collection of unspent 
	 * transaction outputs) is utxoPool. This should make a defensive copy of 
	 * utxoPool by using the UTXOPool(UTXOPool uPool) constructor.
	 */
	public TxHandler(UTXOPool utxoPool) {
        // Make a defensive copy of the UTXOPool
        this.utxoPool = new UTXOPool(utxoPool);
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
        UTXOPool seenUTXOs = new UTXOPool();  // Track UTXOs we've already seen to prevent double spending
        double inputSum = 0;
        double outputSum = 0;

        for (int i = 0; i < tx.numInputs(); i++) {
            Transaction.Input input = tx.getInput(i);
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);

            // (1) Ensure the UTXO exists in the current UTXOPool
            if (!utxoPool.contains(utxo)) {
                return false;
            }

            Transaction.Output output = utxoPool.getTxOutput(utxo);
            RSAKey pubKey = (RSAKey) output.address; // Cast to RSAKey since it's not PublicKey

            // (2) Validate the signature for each input using RSAKey's verifySignature
            if (!pubKey.verifySignature(tx.getRawDataToSign(i), input.signature)) {
                return false;
            }

            // (3) Ensure no UTXO is claimed more than once
            if (seenUTXOs.contains(utxo)) {
                return false;
            }
            seenUTXOs.addUTXO(utxo, output);

            inputSum += output.value;
		}
	}

	/* Handles each epoch by receiving an unordered array of proposed 
	 * transactions, checking each transaction for correctness, 
	 * returning a mutually valid array of accepted transactions, 
	 * and updating the current UTXO pool as appropriate.
	 */
	public Transaction[] handleTxs(Transaction[] possibleTxs) {
		ArrayList<Transaction> validTxs = new ArrayList<>();

        for (Transaction tx : possibleTxs) {
            if (isValidTx(tx)) {
                validTxs.add(tx);

                // Update UTXO pool: remove spent UTXOs and add new UTXOs
                // Remove spent UTXOs
                for (int i = 0; i < tx.numInputs(); i++) {
                    Transaction.Input input = tx.getInput(i);
                    UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
                    utxoPool.removeUTXO(utxo);
                }

                // Add new UTXOs (outputs of the transaction)
                for (int i = 0; i < tx.numOutputs(); i++) {
                    Transaction.Output output = tx.getOutput(i);
                    UTXO newUtxo = new UTXO(tx.getHash(), i);
                    utxoPool.addUTXO(newUtxo, output);
                }
            }
        }

        // Convert list of valid transactions to an array
        return validTxs.toArray(new Transaction[validTxs.size()]);
    }
}