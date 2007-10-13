/*
 *  MicroEmulator
 *  Copyright (C) 2001-2006 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package javax.microedition.pki;

import java.io.IOException;

public class CertificateException extends IOException {

    private static final long serialVersionUID = 1L;

    public static final byte BAD_EXTENSIONS = 1;
	public static final byte CERTIFICATE_CHAIN_TOO_LONG = 2;
	public static final byte EXPIRED = 3;
	public static final byte UNAUTHORIZED_INTERMEDIATE_CA = 4;
	public static final byte MISSING_SIGNATURE = 5;
	public static final byte NOT_YET_VALID = 6;
	public static final byte SITENAME_MISMATCH = 7;
	public static final byte UNRECOGNIZED_ISSUER = 8;
	public static final byte UNSUPPORTED_SIGALG = 9;
	public static final byte INAPPROPRIATE_KEY_USAGE = 10;
	public static final byte BROKEN_CHAIN = 11;
	public static final byte ROOT_CA_EXPIRED = 12;
	public static final byte UNSUPPORTED_PUBLIC_KEY_TYPE = 13;
	public static final byte VERIFICATION_FAILED = 14;
	
	private Certificate certificate;
	private byte status;
	
	public CertificateException(Certificate certificate, byte status) {
		super();
		
		this.certificate = certificate;
		this.status = status;
	}
	
	public CertificateException(String message, Certificate certificate, byte status) {
		super(message);
		
		this.certificate = certificate;
		this.status = status;
	}
	
	public Certificate getCertificate() {
		return certificate;
	}
	
	public byte getReason() {
		return status;
	}
	
}
