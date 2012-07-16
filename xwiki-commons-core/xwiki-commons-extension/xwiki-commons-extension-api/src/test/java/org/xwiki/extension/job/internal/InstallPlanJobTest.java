/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.extension.job.internal;

import junit.framework.Assert;

import org.junit.Test;
import org.xwiki.extension.CoreExtension;
import org.xwiki.extension.ExtensionId;
import org.xwiki.extension.InstallException;
import org.xwiki.extension.LocalExtension;
import org.xwiki.extension.TestResources;
import org.xwiki.extension.job.plan.ExtensionPlan;
import org.xwiki.extension.job.plan.ExtensionPlanAction;
import org.xwiki.extension.job.plan.ExtensionPlanAction.Action;
import org.xwiki.extension.job.plan.ExtensionPlanNode;
import org.xwiki.extension.test.AbstractExtensionHandlerTest;

public class InstallPlanJobTest extends AbstractExtensionHandlerTest
{
    @Test
    public void testInstallPlanWithSimpleRemoteExtensionOnRoot() throws Throwable
    {
        ExtensionPlan plan = installPlan(TestResources.REMOTE_SIMPLE_ID, null);

        // Tree

        Assert.assertEquals(1, plan.getTree().size());

        ExtensionPlanNode node = plan.getTree().iterator().next();

        ExtensionPlanAction action = node.getAction();

        Assert.assertEquals(TestResources.REMOTE_SIMPLE_ID, action.getExtension().getId());
        Assert.assertEquals(Action.INSTALL, node.getAction().getAction());
        Assert.assertNull(action.getPreviousExtension());
        Assert.assertNull(action.getNamespace());
        Assert.assertEquals(0, node.getChildren().size());

        // Actions

        Assert.assertEquals(1, plan.getActions().size());

        Assert.assertSame(action, plan.getActions().iterator().next());
    }

    @Test
    public void testInstallPlanWithRemoteDependencyOnRoot() throws Throwable
    {
        ExtensionPlan plan = installPlan(TestResources.REMOTE_WITHRDEPENDENCY_ID, null);

        Assert.assertEquals(1, plan.getTree().size());

        ExtensionPlanNode node = plan.getTree().iterator().next();

        Assert.assertEquals(TestResources.REMOTE_WITHRDEPENDENCY_ID, node.getAction().getExtension().getId());
        Assert.assertEquals(Action.INSTALL, node.getAction().getAction());
        Assert.assertNull(node.getAction().getPreviousExtension());
        Assert.assertNull(node.getAction().getNamespace());
        Assert.assertEquals(1, node.getChildren().size());

        ExtensionPlanNode childnode = node.getChildren().iterator().next();

        Assert.assertEquals(TestResources.REMOTE_SIMPLE_ID, childnode.getAction().getExtension().getId());
        Assert.assertEquals(Action.INSTALL, node.getAction().getAction());
        Assert.assertNull(node.getAction().getPreviousExtension());
        Assert.assertNull(childnode.getAction().getNamespace());
        Assert.assertTrue(childnode.getChildren().isEmpty());
    }

    @Test
    public void testInstallPlanWithCoreDependencyOnRoot() throws Throwable
    {
        ExtensionPlan plan = installPlan(TestResources.REMOTE_WITHCDEPENDENCY_ID, null);

        Assert.assertEquals(1, plan.getTree().size());

        ExtensionPlanNode node = plan.getTree().iterator().next();

        Assert.assertEquals(TestResources.REMOTE_WITHCDEPENDENCY_ID, node.getAction().getExtension().getId());
        Assert.assertEquals(Action.INSTALL, node.getAction().getAction());
        Assert.assertNull(node.getAction().getPreviousExtension());
        Assert.assertNull(node.getAction().getNamespace());
        Assert.assertEquals(1, node.getChildren().size());

        ExtensionPlanNode childnode = node.getChildren().iterator().next();

        Assert.assertTrue(childnode.getAction().getExtension() instanceof CoreExtension);
        Assert.assertEquals(TestResources.CORE_ID, childnode.getAction().getExtension().getId());
        Assert.assertEquals(Action.NONE, childnode.getAction().getAction());
        Assert.assertNull(node.getAction().getPreviousExtension());
        Assert.assertTrue(childnode.getChildren().isEmpty());
    }

    @Test
    public void testInstallPlanWithInstalledDependencyOnRoot() throws Throwable
    {
        ExtensionPlan plan = installPlan(TestResources.REMOTE_WITHLDEPENDENCY_ID, null);

        Assert.assertEquals(1, plan.getTree().size());

        ExtensionPlanNode node = plan.getTree().iterator().next();

        Assert.assertEquals(TestResources.REMOTE_WITHLDEPENDENCY_ID, node.getAction().getExtension().getId());
        Assert.assertEquals(Action.INSTALL, node.getAction().getAction());
        Assert.assertNull(node.getAction().getPreviousExtension());
        Assert.assertNull(node.getAction().getNamespace());
        Assert.assertEquals(1, node.getChildren().size());

        ExtensionPlanNode childnode = node.getChildren().iterator().next();

        Assert.assertTrue(childnode.getAction().getExtension() instanceof LocalExtension);
        Assert.assertEquals(TestResources.INSTALLED_ID, childnode.getAction().getExtension().getId());
        Assert.assertEquals(Action.NONE, childnode.getAction().getAction());
        Assert.assertNull(node.getAction().getPreviousExtension());
        Assert.assertTrue(childnode.getChildren().isEmpty());
    }

    @Test
    public void testInstallPlanWithUpgradeOnRoot() throws Throwable
    {
        install(TestResources.REMOTE_UPGRADE10_ID, null);

        // //////////////////
        // Test upgrade

        ExtensionPlan plan = installPlan(TestResources.REMOTE_UPGRADE20_ID, null);

        Assert.assertEquals(1, plan.getTree().size());

        ExtensionPlanNode node = plan.getTree().iterator().next();

        ExtensionPlanAction action = node.getAction();

        Assert.assertEquals(TestResources.REMOTE_UPGRADE20_ID, action.getExtension().getId());
        Assert.assertEquals(Action.UPGRADE, action.getAction());
        Assert.assertEquals(TestResources.REMOTE_UPGRADE10_ID, action.getPreviousExtension().getId());
        Assert.assertNull(action.getNamespace());
        Assert.assertEquals(0, node.getChildren().size());
    }

    @Test
    public void testInstallPlanWithDowngradeOnRoot() throws Throwable
    {
        install(TestResources.REMOTE_UPGRADE20_ID, null);

        // //////////////////
        // Test downgrade

        ExtensionPlan plan = installPlan(TestResources.REMOTE_UPGRADE10_ID, null);

        Assert.assertEquals(1, plan.getTree().size());

        ExtensionPlanNode node = plan.getTree().iterator().next();

        ExtensionPlanAction action = node.getAction();

        Assert.assertEquals(TestResources.REMOTE_UPGRADE10_ID, action.getExtension().getId());
        Assert.assertEquals(Action.DOWNGRADE, action.getAction());
        Assert.assertEquals(TestResources.REMOTE_UPGRADE20_ID, action.getPreviousExtension().getId());
        Assert.assertNull(action.getNamespace());
        Assert.assertEquals(0, node.getChildren().size());
    }

    @Test
    public void testInstallPlanWithUnsupportedType() throws Throwable
    {
        try {
            installPlan(TestResources.REMOTE_UNSUPPORTED_ID, null);

            Assert.fail("Should have failed");
        } catch (InstallException e) {
            // expected
        }
    }

    @Test
    public void testInstallPlanWithDependenciesCollision() throws Throwable
    {
        // greater last

        ExtensionPlan /*plan = installPlan(new ExtensionId("dependenciescollision", "1.0"), null);

        Assert.assertEquals(new ExtensionId("upgrade", "2.0"), plan.getActions().iterator().next().getExtension()
            .getId());*/

        // smaller last

        plan = installPlan(new ExtensionId("dependenciescollision", "2.0"), null);

        Assert.assertEquals(new ExtensionId("upgrade", "2.0"), plan.getActions().iterator().next().getExtension()
            .getId());
    }
}
