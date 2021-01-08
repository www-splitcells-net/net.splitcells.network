package net.splitcells.gel.novērtējums.struktūra;

import java.util.List;

import net.splitcells.gel.ierobežojums.GrupaId;
import net.splitcells.gel.ierobežojums.Ierobežojums;

public interface VietējieNovērtējums {

	GrupaId radītsIerobežojumuGrupaId();

	Novērtējums novērtējums();

	List<Ierobežojums> izdalīUz();

	VietējieNovērtējumsI arIzdalīšanaUz(List<Ierobežojums> IzdalīšanaUz);

	VietējieNovērtējumsI arNovērtējumu(Novērtējums novērtējums);

	VietējieNovērtējumsI arRadītuGrupasId(GrupaId radītsIerobežojumuGrupaId);
}
