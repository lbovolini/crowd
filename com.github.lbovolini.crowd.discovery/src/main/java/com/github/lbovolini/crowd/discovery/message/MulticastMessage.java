package com.github.lbovolini.crowd.discovery.message;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class MulticastMessage {

    private final byte[] data;
    private final int dataLength;
    private final InetSocketAddress address;

    public MulticastMessage(byte[] data, int dataLength, InetSocketAddress address) {
        this.data = data;
        this.dataLength = dataLength;
        this.address = address;
    }

    public byte getType() {
        return Objects.requireNonNull(data)[0];
    }

    public byte[] getData() {
        return data;
    }

    public String getDataAsString() {
        return new String(data, 0, dataLength, StandardCharsets.UTF_8);
    }

    /**
     * Retorna o endereço do host remoto.
     * Se utilizado durante a leitura, representa o endereço do host remoto que está enviando a mensagem para o host local.
     * Se utilizado durante a escrita, representa o endereço do host remoto de destina da mensagem.
     * @return Endereço do host remoto.
     */
    public InetSocketAddress getAddress() {
        return address;
    }

    public int getDataLength() {
        return dataLength;
    }

}
