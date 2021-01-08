package net.splitcells.gel.rating.struktūra;

import java.util.List;

import net.splitcells.gel.constraint.GrupaId;
import net.splitcells.gel.constraint.Ierobežojums;

public interface VietējieNovērtējums {

	GrupaId radītsIerobežojumuGrupaId();

	Novērtējums novērtējums();

	List<Ierobežojums> izdalīUz();

	VietējieNovērtējumsI arIzdalīšanaUz(List<Ierobežojums> IzdalīšanaUz);

	VietējieNovērtējumsI arNovērtējumu(Novērtējums novērtējums);

	VietējieNovērtējumsI arRadītuGrupasId(GrupaId radītsIerobežojumuGrupaId);
}
