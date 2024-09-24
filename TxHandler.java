import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.util.ArrayList;
import java.util.HashSet;


public class TxHandler {

    private UTXOPool utxoPool;

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
        UTXOPool seenUTXOs = new UTXOPool(); // Track UTXOs we've already seen to prevent double spending
        double inputSum = 0;
        double outputSum = 0;

        for (int i = 0; i < tx.numInputs(); i++) {
            Transaction.Input input = tx.getInput(i);
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);

            // Ensure the UTXO exists in the current UTXOPool
            if (!utxoPool.contains(utxo)) {
                return false;
            }

            Transaction.Output output = utxoPool.getTxOutput(utxo);
            RSAKey pubKey = (RSAKey) output.address; // Cast to RSAKey since it's not PublicKey

            if (!pubKey.verifySignature(tx.getRawDataToSign(i), input.signature)) {
                return false;
            }

            // Ensure no UTXO is claimed more than once
            if (seenUTXOs.contains(utxo)) {
                return false;
            }
            seenUTXOs.addUTXO(utxo, output);

            inputSum += output.value;
		}
         // Validate outputs
        for (int i = 0; i < tx.numOutputs(); i++) {
            Transaction.Output output = tx.getOutput(i);
            // (4) Ensure all output values are non-negative
            if (output.value < 0) {
                return false;
            }
            outputSum += output.value;
        }

        // (5) Ensure the sum of input values is greater than or equal to the sum of output values
        return inputSum >= outputSum;
    }
	

	/* Handles each epoch by receiving an unordered array of proposed 
	 * transactions, checking each transaction for correctness, 
	 * returning a mutually valid array of accepted transactions, 
	 * and updating the current UTXO pool as appropriate.
	 */
	public Transaction[] handleTxs(Transaction[] possibleTxs) {
		ArrayList<Transaction> validArray = new ArrayList<>(); // create List of valid transactions

        // Iterate through all possible transactions
        for (Transaction tx : possibleTxs) {
            if (isValidTx(tx)) {
                validArray.add(tx); // if valid add to list

                // Remove spent UTXOs (inputs of the transaction)
                for (int i = 0; i < tx.numInputs(); i++) {
                    Transaction.Input input = tx.getInput(i); // get input
                    UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex); // create UTXO
                    utxoPool.removeUTXO(utxo); // remove UTXO from pool
                }

                // Add new UTXOs, outputs of the transaction
                for (int i = 0; i < tx.numOutputs(); i++) {
                    Transaction.Output output = tx.getOutput(i); // get output
                    UTXO createUtxo = new UTXO(tx.getHash(), i); // create new UTXO
                    utxoPool.addUTXO(createUtxo, output); // add UTXO to pool
                }
            }
        }
        return validArray.toArray(new Transaction[validArray.size()]); // return valid transactions
    }
}