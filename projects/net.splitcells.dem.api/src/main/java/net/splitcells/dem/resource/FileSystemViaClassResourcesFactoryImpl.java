/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

public class FileSystemViaClassResourcesFactoryImpl implements FileSystemViaClassResourcesFactoryApi {
    public static FileSystemViaClassResourcesFactoryApi _fileSystemViaClassResourcesFactoryImpl() {
        return new FileSystemViaClassResourcesFactoryImpl();
    }

    private FileSystemViaClassResourcesFactoryImpl() {

    }

    @Override
    public FileSystemView fileSystemViaClassResources(Class<?> clazz, String groupId, String artifactId) {
        return FileSystemViaClassResourcesImpl.fileSystemViaClassResourcesImpl(clazz, groupId, artifactId);
    }
}
