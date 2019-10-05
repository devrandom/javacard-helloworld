package org.gitian.javacard;

import com.licel.jcardsim.bouncycastle.util.encoders.Hex;
import com.licel.jcardsim.smartcardio.CardSimulator;
import com.licel.jcardsim.utils.AIDUtil;
import javacard.framework.AID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

class HelloWorldAppletTest {
    private static CardSimulator simulator;

    @BeforeAll
    static void initAll() throws Exception {
        openSimulatorChannel();
    }

    private static void openSimulatorChannel() throws Exception {
        simulator = new CardSimulator();

        // Install applet
        String PACKAGE_AID_HEX = "0F00BA00000001";
        byte[] aid_bytes = Hex.decode(PACKAGE_AID_HEX + "01");

        AID aid = AIDUtil.create(aid_bytes);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(aid_bytes.length);
        bos.write(aid_bytes);

        simulator.installApplet(aid, HelloWorldApplet.class, bos.toByteArray(), (short) 0, (byte) bos.size());
        bos.close();

        simulator.selectApplet(aid);
    }

    @Test
    void testPing() {
        CommandAPDU apdu = new CommandAPDU(0x80, 0x00, 0, 0);
        ResponseAPDU result = simulator.transmitCommand(apdu);
        assertEquals(result.getSW(), 0x9000);
        assertArrayEquals(result.getData(), "Hello".getBytes());
    }
}
