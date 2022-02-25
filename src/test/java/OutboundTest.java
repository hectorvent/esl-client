import com.google.common.base.Throwables;

import org.freeswitch.esl.client.dptools.Execute;
import org.freeswitch.esl.client.dptools.ExecuteException;
import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.internal.Context;
import org.freeswitch.esl.client.outbound.IClientHandler;
import org.freeswitch.esl.client.outbound.SocketClient;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.freeswitch.esl.client.transport.message.EslHeaders.Name;
import org.freeswitch.esl.client.transport.message.EslMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class OutboundTest {
    private static final Logger logger = LoggerFactory.getLogger(OutboundTest.class);

    public OutboundTest() {
        try {

            final Client inboudClient = new Client();
            inboudClient.connect(new InetSocketAddress("localhost", 8021), "ClueCon", 10);
            inboudClient.addEventListener((ctx, event) -> logger.info("INBOUND onEslEvent: {}", event.getEventName()));

            EslMessage schedHangup1 = inboudClient.sendApiCommand("sched_hangup", "+0 91d60505-b835-44d4-8ff4-87497a0a539e ORIGINATOR_CANCEL");


            String s = nameMapToString(schedHangup1.getHeaders(), schedHangup1.getBodyLines());
            System.out.println(s);

//            for (Map.Entry kv: schedHangup.getHeaders().entrySet()) {
//                System.out.println(kv.getKey() +" = " + kv.getValue());
//            }
//            System.out.println("");
//
//            for (String l : schedHangup.getBodyLines()) {
//                System.out.println(l);
//            }


            CompletableFuture<EslEvent> schedHangup = inboudClient.sendBackgroundApiCommand("sched_hangup", "+0 91d60505-b835-44d4-8ff4-87497a0a539e ORIGINATOR_CANCEL");

            schedHangup.whenComplete((ev, er) -> {

                System.out.println("here......");
            for (Map.Entry kv: ev.getEventHeaders().entrySet()) {
                System.out.println(kv.getKey() +" = " + kv.getValue());
            }
            System.out.println("");

            for (String l : ev.getEventBodyLines()) {
                System.out.println(l);
            }
            });
            System.out.println("Comple");

            Thread.sleep(3000);

            System.out.println("Done");

//            final SocketClient outboundServer = new SocketClient(
//                    new InetSocketAddress("localhost", 8084),
//                    () -> new IClientHandler() {
//                        @Override
//                        public void onConnect(Context context,
//                                EslEvent eslEvent) {
//
//
//                            logger.warn(nameMapToString(eslEvent
//                                    .getMessageHeaders(), eslEvent.getEventBodyLines()));
//
//                            String uuid = eslEvent.getEventHeaders()
//                                    .get("unique-id");
//
//                            logger.warn(
//                                    "Creating execute app for uuid {}",
//                                    uuid);
//
//                            Execute exe = new Execute(context, uuid);
//
//                            try {
//
//                                exe.answer();
//
//                                String digits = exe.playAndGetDigits(3,
//                                        5, 10, 10 * 1000, "#", prompt,
//                                        failed, "^\\d+", 10 * 1000);
//                                logger.warn("Digits collected: {}",
//                                        digits);
//
//
//                            } catch (ExecuteException e) {
//                                logger.error(
//                                        "Could not prompt for digits",
//                                        e);
//
//                            } finally {
//                                try {
//                                    exe.hangup(null);
//                                } catch (ExecuteException e) {
//                                    logger.error(
//                                            "Could not hangup",e);
//                                }
//                            }
//
//                        }
//
//                        @Override
//                        public void onEslEvent(Context ctx,
//                                EslEvent event) {
//                            logger.info("OUTBOUND onEslEvent: {}",
//                                    event.getEventName());
//
//                        }
//                    });
//            outboundServer.startAsync();

        } catch (Throwable t) {
            Throwables.propagate(t);
        }
    }

    public static void main(String[] args) {
        new OutboundTest();
    }

    public static String nameMapToString(Map<Name, String> map,
                                         List<String> lines) {
        StringBuilder sb = new StringBuilder("\nHeaders:\n");
        for (Name key : map.keySet()) {
            if (key == null)
                continue;
            sb.append(key);
            sb.append("\t\t\t\t = \t ");
            sb.append(map.get(key));
            sb.append("\n");
        }
        if (lines != null) {
            sb.append("Body Lines:\n");
            for (String line : lines) {
                sb.append(line);
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
