package net.splitcells.gel.dati.piešķiršanas;


import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.dati.datubāze.DatuBāze;

public interface PiešķiršanasVeidotajs extends Closeable, Flushable {
    Piešķiršanas piešķiršanas(String vārds, DatuBāze prāsibas, DatuBāze piedāvājumi);
}
