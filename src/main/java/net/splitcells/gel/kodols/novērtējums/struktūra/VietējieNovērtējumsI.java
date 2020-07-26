package net.splitcells.gel.kodols.novērtējums.struktūra;

import java.util.List;

import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.ierobežojums.GrupaId;

public class VietējieNovērtējumsI implements VietējieNovērtējums {

	private GrupaId radītsIerobežojumuGrupaId;
	private Novērtējums novērtējums;
	private List<Ierobežojums> izdalīUz;

	public static VietējieNovērtējums lokalsNovērtejums() {
		return new VietējieNovērtējumsI();
	}

	private VietējieNovērtējumsI() {

	}

	@Override
	public GrupaId radītsIerobežojumuGrupaId() {
		return radītsIerobežojumuGrupaId;
	}

	@Override
	public Novērtējums novērtējums() {
		return novērtējums;
	}

	@Override
	public List<Ierobežojums> izdalīUz() {
		return izdalīUz;
	}

	@Override
	public VietējieNovērtējumsI arIzdalīšanaUz(List<Ierobežojums> IzdalīšanaUz) {
		this.izdalīUz = IzdalīšanaUz;
		return this;
	}

	@Override
	public VietējieNovērtējumsI arNovērtējumu(Novērtējums novērtējums) {
		this.novērtējums = novērtējums;
		return this;
	}

	@Override
	public VietējieNovērtējumsI arRadītuGrupasId(GrupaId radītsIerobežojumuGrupaId) {
		this.radītsIerobežojumuGrupaId = radītsIerobežojumuGrupaId;
		return this;
	}

}
