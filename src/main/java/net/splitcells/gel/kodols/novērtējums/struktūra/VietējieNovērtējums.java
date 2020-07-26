package net.splitcells.gel.kodols.novērtējums.struktūra;

import java.util.List;

import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.ierobežojums.GrupaId;

public interface VietējieNovērtējums {

	GrupaId radītsIerobežojumuGrupaId();

	Novērtējums novērtējums();

	List<Ierobežojums> izdalīUz();

	VietējieNovērtējumsI arIzdalīšanaUz(List<Ierobežojums> IzdalīšanaUz);

	VietējieNovērtējumsI arNovērtējumu(Novērtējums novērtējums);

	VietējieNovērtējumsI arRadītuGrupasId(GrupaId radītsIerobežojumuGrupaId);
}
