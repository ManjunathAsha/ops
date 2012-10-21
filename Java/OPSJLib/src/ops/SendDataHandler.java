/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ops;

/**
 *
 * @author staxgr
 */
public interface SendDataHandler
{
    void addPublisher(Publisher pub);
    boolean removePublisher(Publisher pub);
    boolean sendData(byte[] bytes, int size, Topic t);
}
