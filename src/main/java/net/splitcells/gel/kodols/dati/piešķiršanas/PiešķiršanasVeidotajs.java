package net.splitcells.gel.kodols.dati.piešķiršanas;


import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.kodols.dati.datubāze.DatuBāze;

public interface PiešķiršanasVeidotajs extends Closeable, Flushable {
    Piešķiršanas piešķiršanas(String vārds, DatuBāze prāsibas, DatuBāze piedāvājumi);
}
