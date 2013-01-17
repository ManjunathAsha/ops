/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package opstestapp;

import ops.OPSObject;

/**
 *
 * @author Lelle
 */
public interface IOpsHelperListener extends ILogListener
{
    void OnData(String topName, OPSObject arg);
}
