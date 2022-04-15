package process;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BGPMessage {

    private String host;
    private String type;
    private Integer peerASn;
    private String timeStamp;
    private List<Integer> path;
    private List<String> prefixes;
    private List<String> communities;

    public BGPMessage() {
    }

    @JsonSetter("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonSetter("timestamp")
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @JsonSetter("prefix")
    public void setPrefixes(List<String> prefixes) {
        this.prefixes = prefixes;
    }

    @JsonSetter("peer_asn")
    public void setPeerASn(Integer peerASn) {
        this.peerASn = peerASn;
    }

    @JsonSetter("path")
    public void setPath(List<Integer> path) {
        this.path = path;
    }

    @JsonSetter("host")
    public void setHost(String host) {
        this.host = host;
    }

    @JsonSetter("communities")
    public void setCommunities(List<String> communities) {
        this.communities = communities;
    }

    public String getType() {
        return type;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public List<String> getPrefixes() {
        return prefixes;
    }

    public Integer getPeerASn() {
        return peerASn;
    }

    public List<Integer> getPath() {
        return path;
    }

    public String getHost() {
        return host;
    }

    public List<String> getCommunities() {
        return communities;
    }

    @Override
    public String toString() {
        return "BGPMessage{" +
                "type='" + type + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", prefixes=" + prefixes +
                ", peerASn=" + peerASn +
                ", path=" + path +
                ", host='" + host + '\'' +
                ", communities=" + communities +
                '}';
    }

    public BGPMessage cleanLoops() {
        path = clean(path);
        return this;
    }

    public static List<Integer> cleanLoops(List<Integer> path) {
        path = clean(path);
        return path;
    }

    private static List<Integer> clean(List<Integer> path) {
        Collections.reverse(path);
        List<Integer> newSeqInv = new ArrayList<>();

        for (int x : path) {
            if (newSeqInv.contains(x)) {
                newSeqInv = newSeqInv.subList(0, newSeqInv.indexOf(x) + 1);
            } else newSeqInv.add(x);
        }
        Collections.reverse(newSeqInv);
        path = newSeqInv;
        return path;
    }
}
