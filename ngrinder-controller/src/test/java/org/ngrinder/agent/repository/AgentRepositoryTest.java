/*
 * Copyright (C) 2012 - 2012 NHN Corporation
 * All rights reserved.
 *
 * This file is part of The nGrinder software distribution. Refer to
 * the file LICENSE which is part of The nGrinder distribution for
 * licensing details. The nGrinder distribution is available on the
 * Internet at http://nhnopensource.org/ngrinder
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.ngrinder.agent.repository;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import net.grinder.message.console.AgentControllerState;

import org.junit.Before;
import org.junit.Test;
import org.ngrinder.AbstractNGrinderTransactionalTest;
import org.ngrinder.agent.model.AgentInfo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test AgentRepository Class.
 * 
 * @author Mavlarn
 * @since
 */
public class AgentRepositoryTest extends AbstractNGrinderTransactionalTest {

	@Autowired
	private AgentRepository agentRepository;

	AgentInfo agentInfo;

	@Before
	public void before() {
		agentRepository.deleteAll();
		agentInfo = new AgentInfo();
		agentInfo.setHostName("hello");
		agentInfo.setIp("127.0.0.1");
		agentInfo.setRegion("world");
		agentInfo.setStatus(AgentControllerState.BUSY);
		agentRepository.save(agentInfo);
	}

	@Test
	public void testGetByIp() {
		assertThat(agentRepository.findByIp("127.0.0.1"), notNullValue());
		assertThat(agentRepository.findByIp("127.0.0.1").getHostName(), is("hello"));
		assertThat(agentRepository.findByIp("127.0.0.1").getRegion(), is("world"));
	}
}