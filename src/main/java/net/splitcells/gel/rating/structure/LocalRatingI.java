package net.splitcells.gel.rating.structure;

import java.util.List;

import net.splitcells.gel.constraint.GrupaId;
import net.splitcells.gel.constraint.Ierobežojums;

public class LocalRatingI implements LocalRating {

	private GrupaId radītsIerobežojumuGrupaId;
	private Rating novērtējums;
	private List<Ierobežojums> izdalīUz;

	public static LocalRating lokalsNovērtejums() {
		return new LocalRatingI();
	}

	private LocalRatingI() {

	}

	@Override
	public GrupaId radītsIerobežojumuGrupaId() {
		return radītsIerobežojumuGrupaId;
	}

	@Override
	public Rating novērtējums() {
		return novērtējums;
	}

	@Override
	public List<Ierobežojums> izdalīUz() {
		return izdalīUz;
	}

	@Override
	public LocalRatingI arIzdalīšanaUz(List<Ierobežojums> IzdalīšanaUz) {
		this.izdalīUz = IzdalīšanaUz;
		return this;
	}

	@Override
	public LocalRatingI arNovērtējumu(Rating novērtējums) {
		this.novērtējums = novērtējums;
		return this;
	}

	@Override
	public LocalRatingI arRadītuGrupasId(GrupaId radītsIerobežojumuGrupaId) {
		this.radītsIerobežojumuGrupaId = radītsIerobežojumuGrupaId;
		return this;
	}

}
