import java.util.ArrayList;

public class TxHandler {

    private UTXOPool currentUtxoPool;

    /* Initializes the handler with a given UTXOPool (collection of unspent 
     * transaction outputs). This creates a defensive copy of the provided 
     * UTXOPool using the UTXOPool(UTXOPool uPool) constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        this.currentUtxoPool = new UTXOPool(utxoPool); // create a defensive copy of the UTXOPool
    }

    /* Validates a transaction based on the following criteria:
     * (1) All outputs claimed by the transaction are in the current UTXO pool,
     * (2) The signatures on each input of the transaction are valid,
     * (3) No UTXO is claimed more than once by the transaction,
     * (4) All output values of the transaction are non-negative, and
     * (5) The sum of input values is at least as large as the sum of output values;
     * otherwise, returns false.
     */
    public boolean isValidTx(Transaction transaction) {
        UTXOPool seenUtxos = new UTXOPool(); // track UTXOs seen so far
        double totalInputValue = 0; // track total input value
        double totalOutputValue = 0; // track total output value

        // Iterate through each input in the transaction
        for (int i = 0; i < transaction.numInputs(); i++) {
            Transaction.Input input = transaction.getInput(i); // get the input at index i
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex); // create a UTXO object

            // Ensure all outputs are in the current UTXO pool
            if (!currentUtxoPool.contains(utxo)) {
                return false; // false if not in the pool
            }

            Transaction.Output output = currentUtxoPool.getTxOutput(utxo); // get the output from the UTXO pool
            RSAKey publicKey = (RSAKey) output.address; // cast to RSAKey since it's not PublicKey

            // Verify the signature on each input
            if (!publicKey.verifySignature(transaction.getRawDataToSign(i), input.signature)) {
                return false;
            }

            // Ensure no UTXO is claimed more than once
            if (seenUtxos.contains(utxo)) {
                return false;
            }
            seenUtxos.addUTXO(utxo, output); // add the UTXO to the seen UTXOs pool
            totalInputValue += output.value; // add the output value to the total input value
        }

        // Ensure for all output values in the transaction
        for (Transaction.Output output : transaction.getOutputs()) {
            if (output.value < 0) { // output values are non-negative
                return false; 
            }
            totalOutputValue += output.value; // add the output value to the total output value
        }
        return totalInputValue >= totalOutputValue; // sum of input values >= sum of output values
    }

    /* Processes each epoch by receiving an unordered array of proposed 
     * transactions, validating each transaction, returning an array of 
     * accepted transactions, and updating the current UTXO pool accordingly.
     */
    public Transaction[] handleTxs(Transaction[] proposedTxs) {
        ArrayList<Transaction> validTransactions = new ArrayList<>(); // track valid transactions

        // Iterate through each proposed transaction
        for (Transaction transaction : proposedTxs) {
            if (isValidTx(transaction)) {
                validTransactions.add(transaction); // add it to the list of valid transactions

                // Remove spent UTXOs from the pool
                for (Transaction.Input input : transaction.getInputs()) {
                    UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex); // create a UTXO object
                    currentUtxoPool.removeUTXO(utxo); // remove the UTXO from the pool
                }

                byte[] transactionHash = transaction.getHash(); // get the hash of the transaction

                // Add new UTXOs to the pool
                for (int i = 0; i < transaction.numOutputs(); i++) { 
                    UTXO utxo = new UTXO(transactionHash, i); // create a UTXO object
                    currentUtxoPool.addUTXO(utxo, transaction.getOutput(i)); // add the UTXO to the pool
                }
            }
        }
        Transaction[] acceptedTransactions = new Transaction[validTransactions.size()]; // create an array of accepted transactions
        return validTransactions.toArray(acceptedTransactions); // return the array of accepted transactions
    }
}