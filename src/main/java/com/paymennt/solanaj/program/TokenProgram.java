/************************************************************************ 
 * Copyright PointCheckout, Ltd.
 * 
 */
package com.paymennt.solanaj.program;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.paymennt.solanaj.data.AccountMeta;
import com.paymennt.solanaj.data.SolanaPublicKey;
import com.paymennt.solanaj.data.SolanaTransactionInstruction;

/**
 * 
 */
public class TokenProgram {

    /**  */
    public static final SolanaPublicKey PROGRAM_ID =
            new SolanaPublicKey("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");
    
    /**  */
    private static final SolanaPublicKey SYSVAR_RENT_PUBKEY =
            new SolanaPublicKey("SysvarRent111111111111111111111111111111111");

    /**  */
    private static final int INITIALIZE_METHOD_ID = 1;
    
    /**  */
    private static final int TRANSFER_METHOD_ID = 3;
    
    /**  */
    private static final int CLOSE_ACCOUNT_METHOD_ID = 9;
    
    /**  */
    private static final int TRANSFER_CHECKED_METHOD_ID = 12;

    /**
     * Transfers an SPL token from the owner's source account to destination account.
     * Destination pubkey must be the Token Account (created by token mint), and not the main SOL address.
     * @param source SPL token wallet funding this transaction
     * @param destination Destined SPL token wallet
     * @param amount 64 bit amount of tokens to send
     * @param owner account/private key signing this transaction
     * @return transaction id for explorer
     */
    public static SolanaTransactionInstruction transfer(
            SolanaPublicKey source,
            SolanaPublicKey destination,
            long amount,
            SolanaPublicKey owner) {
        final List<AccountMeta> keys = new ArrayList<>();

        keys.add(new AccountMeta(source, false, true));
        keys.add(new AccountMeta(destination, false, true));
        keys.add(new AccountMeta(owner, true, false));

        byte[] transactionData = encodeTransferTokenInstructionData(amount);

        return new SolanaTransactionInstruction(PROGRAM_ID, keys, transactionData);
    }

    /**
     * 
     *
     * @param source 
     * @param destination 
     * @param amount 
     * @param decimals 
     * @param owner 
     * @param tokenMint 
     * @return 
     */
    public static SolanaTransactionInstruction transferChecked(
            SolanaPublicKey source,
            SolanaPublicKey destination,
            long amount,
            byte decimals,
            SolanaPublicKey owner,
            SolanaPublicKey tokenMint) {
        final List<AccountMeta> keys = new ArrayList<>();

        keys.add(new AccountMeta(source, false, true));
        // index 1 = token mint (https://docs.rs/spl-token/3.1.0/spl_token/instruction/enum.TokenInstruction.html#variant.TransferChecked)
        keys.add(new AccountMeta(tokenMint, false, false));
        keys.add(new AccountMeta(destination, false, true));
        keys.add(new AccountMeta(owner, true, false));

        byte[] transactionData = encodeTransferCheckedTokenInstructionData(amount, decimals);

        return new SolanaTransactionInstruction(PROGRAM_ID, keys, transactionData);
    }

    /**
     * 
     *
     * @param account 
     * @param mint 
     * @param owner 
     * @return 
     */
    public static SolanaTransactionInstruction initializeAccount(
            final SolanaPublicKey account,
            final SolanaPublicKey mint,
            final SolanaPublicKey owner) {
        final List<AccountMeta> keys = new ArrayList<>();

        keys.add(new AccountMeta(account, false, true));
        keys.add(new AccountMeta(mint, false, false));
        keys.add(new AccountMeta(owner, false, true));
        keys.add(new AccountMeta(SYSVAR_RENT_PUBKEY, false, false));

        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) INITIALIZE_METHOD_ID);

        return new SolanaTransactionInstruction(PROGRAM_ID, keys, buffer.array());
    }

    /**
     * 
     *
     * @param source 
     * @param destination 
     * @param owner 
     * @return 
     */
    public static SolanaTransactionInstruction closeAccount(
            final SolanaPublicKey source,
            final SolanaPublicKey destination,
            final SolanaPublicKey owner) {
        final List<AccountMeta> keys = new ArrayList<>();

        keys.add(new AccountMeta(source, false, true));
        keys.add(new AccountMeta(destination, false, true));
        keys.add(new AccountMeta(owner, true, false));

        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) CLOSE_ACCOUNT_METHOD_ID);

        return new SolanaTransactionInstruction(PROGRAM_ID, keys, buffer.array());
    }

    /**
     * 
     *
     * @param amount 
     * @return 
     */
    private static byte[] encodeTransferTokenInstructionData(long amount) {
        ByteBuffer result = ByteBuffer.allocate(9);
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.put((byte) TRANSFER_METHOD_ID);
        result.putLong(amount);

        return result.array();
    }

    /**
     * 
     *
     * @param amount 
     * @param decimals 
     * @return 
     */
    private static byte[] encodeTransferCheckedTokenInstructionData(long amount, byte decimals) {
        ByteBuffer result = ByteBuffer.allocate(10);
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.put((byte) TRANSFER_CHECKED_METHOD_ID);
        result.putLong(amount);
        result.put(decimals);

        return result.array();
    }
}