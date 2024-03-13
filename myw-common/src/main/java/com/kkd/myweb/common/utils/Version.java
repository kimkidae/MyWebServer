/*
 * Copyright 2013-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kkd.myweb.common.utils;

import java.util.Objects;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

/**
 * Value object to represent a Version consisting of major, minor and bugfix part.
 */
public class Version implements Comparable<Version> {

	private static final String VERSION_PARSE_ERROR = "Invalid version string; Could not parse segment %s within %s";

	private final int major;
	private final int minor;
	private final int bugfix;

	/**
	 * Creates a new {@link Version} from the given integer values. At least one value has to be given but a maximum of 3.
	 *
	 * @param parts must not be {@literal null} or empty.
	 */
	public Version(int... parts) {

        Objects.requireNonNull(parts, "Parts must not be null");
        assertIsTrue(parts.length > 0 && parts.length < 4, String.format("Invalid parts length: 0 < %s < 4", parts.length));

		this.major = parts[0];
		this.minor = parts.length > 1 ? parts[1] : 0;
		this.bugfix = parts.length > 2 ? parts[2] : 0;

		assertIsTrue(major >= 0, "Major version must be greater or equal zero");
		assertIsTrue(minor >= 0, "Minor version must be greater or equal zero");
		assertIsTrue(bugfix >= 0, "Bugfix version must be greater or equal zero");
	}
    
	private void assertIsTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
    }

    private static void assertHasText(String text, String message) {
		if (!StringUtils.isNotBlank(text)) {
			throw new IllegalArgumentException(message);
		}
	}

    /**
	 * Parses the given string representation of a version into a {@link Version} object.
	 *
	 * @param version must not be {@literal null} or empty.
	 * @return
	 */
	public static Version parse(String version) {

		assertHasText(version, "Version must not be null o empty");

		String[] parts = version.trim().split("\\.");
		int[] intParts = new int[parts.length];

		for (int i = 0; i < parts.length; i++) {

			String input = i == parts.length - 1 ? parts[i].replaceAll("\\D.*", "") : parts[i];

			if (StringUtils.isNotBlank(input)) {
				try {
					intParts[i] = Integer.parseInt(input);
				} catch (IllegalArgumentException o_O) {
					throw new IllegalArgumentException(String.format(VERSION_PARSE_ERROR, input, version), o_O);
				}
			}
		}

		return new Version(intParts);
	}

	/**
	 * Returns the Java version of the running JVM.
	 *
	 * @return will never be {@literal null}.
	 */
	public static Version javaVersion() {
		return parse(System.getProperty("java.version"));
	}

	/**
	 * Returns whether the current {@link Version} is greater (newer) than the given one.
	 *
	 * @param version
	 * @return
	 */
	public boolean isGreaterThan(Version version) {
		return compareTo(version) > 0;
	}

	/**
	 * Returns whether the current {@link Version} is greater (newer) or the same as the given one.
	 *
	 * @param version
	 * @return
	 */
	public boolean isGreaterThanOrEqualTo(Version version) {
		return compareTo(version) >= 0;
	}

	/**
	 * Returns whether the current {@link Version} is the same as the given one.
	 *
	 * @param version
	 * @return
	 */
	public boolean is(Version version) {
		return equals(version);
	}

	/**
	 * Returns whether the current {@link Version} is less (older) than the given one.
	 *
	 * @param version
	 * @return
	 */
	public boolean isLessThan(Version version) {
		return compareTo(version) < 0;
	}

	/**
	 * Returns whether the current {@link Version} is less (older) or equal to the current one.
	 *
	 * @param version
	 * @return
	 */
	public boolean isLessThanOrEqualTo(Version version) {
		return compareTo(version) <= 0;
	}

	public int compareTo(Version that) {

		if (major != that.major) {
			return major - that.major;
		}

		if (minor != that.minor) {
			return minor - that.minor;
		}

		if (bugfix != that.bugfix) {
			return bugfix - that.bugfix;
		}

		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Version that)) {
			return false;
		}

		return this.major == that.major && this.minor == that.minor && this.bugfix == that.bugfix;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result += 31 * major;
		result += 31 * minor;
		result += 31 * bugfix;
		return result;
	}

	@Override
	public String toString() {
        StringJoiner joiner = new StringJoiner(".");
        joiner.add(String.valueOf(major));
        joiner.add(String.valueOf(minor));
        joiner.add(String.valueOf(bugfix));

        return joiner.toString();
	}
}
